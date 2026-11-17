# Prompts Used

## Prompt 1 - Project Design

Follow only the rules mentioned by me, everything I am offering must be respected as it is. Only based by the prompt introduced by me, start with this prompt idea to understand the project requirements:

Start with a design

Read Exercises 1 and 2 in the lab guide and inspect the Java starter.
Propose a Java 8 design for all eight reports and the `-statistics` console.
Keep the existing domain classes and propose a separate `com.luxoft.bankapp.ai` package for the reporting implementation.
Use account IDs as the keys of an insertion-ordered map for bank-wide reports.
Explain how you will handle repeated IDs, balance ties, missing cities, and used overdraft.
List the files and the tests before making changes.

Only complete in "\bankapp\ai" and copy the rest of the files from the previous implementation (my code from "bankapp" and write your improved version after I will give and the next iteration details). Update in the "bankapp\ai" the package paths to be the ones of your AI code generated.

## Prompt 2 - Collections and Domain Contracts

Iteration 1) Collections and domain contracts

Check that `Bank` and `Client` use generic sets with unmodifiable getters, and that mutable city and balance values do not affect equality.
Reuse the domain code where those contracts already hold.
Implement the 8 lab methods in `BankReport` using loops, `LinkedHashMap.putIfAbsent` for bank-wide accounts, `TreeSet` for sorting, and `TreeMap` for city grouping.
Keep the first bank-wide account for each ID, preserve each client's own accounts in the customer map, and break balance ties by account ID.
Show the diff and run the tests.

## Prompt 3 - Project Structure

the correct path to be saved your AI implementation should be in
src\com\luxoft\bankapp-ai\ for the code and the test cases are in
test\com\luxoft\bankapp
follow the same structure of organizing the files like in my offered skeleton for bankapp into your bankapp-ai

## Prompt 4 - Streams Implementation

Iteration 2) Streams

Implement `BankReportStreams` with the same eight signatures.
Use `flatMap` and a first-wins `Collectors.toMap` with `LinkedHashMap::new` for the account index.
Use typed collectors for sorting and grouping and primitive streams for totals.
Share the comparators, and compute each report in the streams class.
Add tests comparing both implementations by value and order. Cover duplicate IDs, equal balances, negative balances, missing cities, and an empty bank.

## Prompt 5 - Implementation Correction

no, this was a copy you've made earlier when I said to copy my structure, is not your code.

## Prompt 6 - Console and Verification

Iteration 3) Console and verification

Add an entry point in the AI package that accepts `-statistics` and reads `display tatistic` and `exit`.
Use the collections report and print all eight results. Test command parsing and run he real console with piped input.
Run the entire Maven test suite.
Summarize the commands and actual outcomes, then compare the AI solution with the riginal implementation using one concrete duplicate-account example.
