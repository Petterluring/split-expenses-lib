
# Introduction

Consider a group of people participating in activities involving expenses where each person has covered different amounts of the total expenses. The goal of this library is to decide how the group members should pay each other to settle the expenses fairly and effectively. While the solution to this problem is trivial when the exepenses are split equally and  covered by one member, more complexity is added when members cover different amounts of the total expenses while each member pays different shares for each expense. 

## Example

Consider a group of four friends: Alice, Bob, Charlie, and Diana going on a weekend trip.

**Expense 1 - Hotel**: Alice pays $200 for accommodation
- Alice: 50% of the cost = $100
- Bob: 30% of the cost = $60
- Charlie: 20% of the cost = $40
- Diana: 0% (doesn't stay at the hotel)

**Expense 2 - Groceries**: Bob pays $120 for groceries
- Splits equally among all four members
- Each person: 25% = $30

**Expense 3 - Activities**: Charlie pays $300 for activity tickets
- Alice: 40% = 120$ (attends 2 out of 3 activities)
- Bob: 40% = 120$ (attends 2 out of 3 activities)
- Charlie: 10% = 30$ (organizer discount)
- Diana: 10% = 30$ (limited activities)


## Initial solution

A trivial solution is to resolve each expense independently. Using Expense 1 as an example, Alice would simply remove her 100$ share from the 200$ expense, leaving 100$ in credit for Bob and Charlie to pay. Their shares are 30% and 20%, meaning that they should pay 60$ and 40$ dollars respectively. This procedure would then repeat for Expense 2 and 3

The solution introduces more payments between group members than needed when comparing it to the better solution introduced in the next section.

## Better solution

The better solution, and perhaps the most effective one, is to subtract each members' total credit with their total debt, yielding a net debt/credit for each member. Then, as a rule of thumb, debts should cancel credits that are similar in value. This means that high debts will cancel high credits, creating larger and fewer payments between group members. 

### Solving example

Net debts/credits:
- Alice (A) = (200-100)\$ - 30$ - 120$ = -50$
- Bob (B) = -60$ + (120-30)\$ - 120$ = -90$
- Charlie (C) = -40$ - 30$ + (300-30)\$ = 200$
- Diana (D) = -30$ - 30$ = -60$

Remark that negative values correspond to net debts while positive values are net credits. 

Put the debts and credits in a list and sort them increasingly. Dollar signs are omitted.

```bash
[-90 (B), -60 (D), -50 (A), 200 (C)]
```

Initialize two pointers at the start and end of the list and successively resolve the debts/credits.

```bash
  p1                         p2
[-90 (B), -60 (D), -50 (A), 200 (C)]
  |                          Λ
  |__________________________|

          p1                p2
[ 0 (B), -60 (D), -50 (A), 110 (C)]
          |                 Λ
          |_________________|

                  p1      p2
[ 0 (B),  0 (D), -50 (A), 50 (C)]
                  |        Λ
                  |________|

[ 0 (B),  0 (D), 0 (A), 0 (C)]
```
Remark that p2 was never moved as there where only one credit. However, if there were more, the pointer would move to the left after the previous credit was paid off.

# Gradle project

## Building and Testing

This project uses Gradle for building and testing. The project was built using Java 21 and is implemented with Kotlin.

**Build the library:**
```bash
./gradlew build
```

**Run tests:**
```bash
./gradlew test
```

The build configuration is defined in `lib/build.gradle.kts` and uses:
- Kotlin JVM plugin for Kotlin support
- Java Library plugin for API/implementation separation
- JUnit Platform for test execution
- ktlint for code formatting

## Source code

The library provides the following main classes:

### `Party` (abstract)

The abstract base class representing an individual in a group who has a debt or credit for a shared expense.

### `Creditor`

Extends `Party` and represents an individual who collects debts from debtors in a group with shared expenses. This is the person who paid for an expense.

### `Debtor`

Extends `Party` and represents an individual owing money to a creditor in a group.

### `Expense`

Represents a shared expense that was paid by one individual (the creditor) in a group. The remaining group members become debtors who pay their share to settle the expense fairly.

### `Payment`

Represents a payment that should be made from a debtor to a creditor.

## Using as a Dependency

The user can add this library as a gradle dependency:
```kotlin
dependencies {
    implementation("io.github.petterluring:split-expenses:1.0.0")
}
```

## Example usage

Here's a comprehensive example showing how to use the library with a weekend trip scenario involving multiple expenses, varying amounts, different shares per expense, and different people covering the costs:

```kotlin
import com.petterluring.splitexpenses.expense.Expense
import com.petterluring.splitexpenses.parties.Creditor
import com.petterluring.splitexpenses.parties.Debtor
import com.petterluring.splitexpenses.payment.Payment

fun main() {
    // Weekend trip with 4 friends: Alice, Bob, Charlie, and Diana

    // Expense 1: Hotel ($200) - Alice pays
    // Alice stays at hotel but others split it differently
    val hotelExpense = Expense(
        name = "Hotel",
        description = "Weekend accommodation",
        amount = 200.0,
        creditor = Creditor("Alice", 0.50),  // Alice pays and stays 50%
        debtors = listOf(
            Debtor("Bob", 0.30),    // Bob stays 30%
            Debtor("Charlie", 0.20) // Charlie stays 20%
            // Diana doesn't stay at the hotel (not included)
        )
    )

    // Expense 2: Groceries ($120) - Bob pays
    // Split equally among all four friends
    val groceriesExpense = Expense(
        name = "Groceries",
        description = "Food for the weekend",
        amount = 120.0,
        creditor = Creditor("Bob", 0.25),
        debtors = listOf(
            Debtor("Alice", 0.25),
            Debtor("Charlie", 0.25),
            Debtor("Diana", 0.25)
        )
    )

    // Expense 3: Activity Tickets ($300) - Charlie pays
    // Different shares based on activity participation
    val activityExpense = Expense(
        name = "Activity Tickets",
        description = "Adventure activities",
        amount = 300.0,
        creditor = Creditor("Charlie", 0.10),  // Charlie is organizer, gets 10%
        debtors = listOf(
            Debtor("Alice", 0.40),   // Alice attends 40%
            Debtor("Bob", 0.40),     // Bob attends 40%
            Debtor("Diana", 0.10)    // Diana attends 10%
        )
    )

    // Expense 4: Restaurant Dinner ($150) - Diana pays
    // Not everyone attends
    val dinnerExpense = Expense(
        name = "Restaurant Dinner",
        description = "Group dinner on Saturday",
        amount = 150.0,
        creditor = Creditor("Diana", 0.0),    // Diana pays and doesn't eat (covers others)
        debtors = listOf(
            Debtor("Alice", 0.33),   // Alice eats 1/3
            Debtor("Bob", 0.33),     // Bob eats 1/3
            Debtor("Charlie", 0.34)  // Charlie eats 1/3
        )
    )

    // Collect all expenses
    val allExpenses = listOf(hotelExpense, groceriesExpense, activityExpense, dinnerExpense)

    // Calculate the global tab showing who owes/is owed money
    val tab = Expense.globalTab(allExpenses)
    println("=== Global Tab ===")
    tab.forEach { (name, amount) ->
        if (amount > 0) {
            println("$name is owed: $${"%.2f".format(amount)}")
        } else {
            println("$name owes: $${"%.2f".format(-amount)}")
        }
    }
    println()

    // Settle all expenses - get the actual payments needed
    val payments = Expense.settle(allExpenses)
    println("=== Required Payments ===")
    payments.forEach { payment ->
        println("${payment.from} pays ${payment.to}: $${"%.2f".format(payment.amount)}")
    }
    println()
}
```