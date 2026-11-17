# Bank Application

Java banking application implementing **Exercises 1 & 2** from `labguide.doc` using Java Collections and Streams. Includes an AI-assisted implementation for comparison.

## Exercises (1 - `BankReport` and 2 - `BankReportStreams`)

- **Bank and Client:** Generic `LinkedHashSet` collections with unmodifiable getters.
- **BankReport:** Uses loops, `TreeSet`, `TreeMap`, and comparators.
- **BankReportStreams:** Uses Java 8 streams, `flatMap`, `distinct`, and collectors.
- **Reporting:** 8 reports covering accounts, clients, balances, cities, and overdraft.
- **Tests:** JUnit 4 starter tests and JUnit 5 reporting tests, including edge cases and implementation comparisons.

## AI-Assisted Solution

The alternative implementation is in `com.luxoft.bankapp.ai`.

See [PROMPTS.md](PROMPTS.md) for the AI prompts and instructions.

## Exercise 3 (Comparison)

Codex was used to develop the AI-assisted solution. The supplied collections implementation served as the reference.

| Feature | Reference | AI implementation |
| :-----: | :-------: | :---------------: |
| Distinct accounts | Set-based deduplication | Typed sets / `distinct()` |
| Balance sorting | Rounded difference | Balance, then account ID |
| City grouping | Manual map updates | `computeIfAbsent` / `groupingBy` |
| Used overdraft | Loop calculation | Loops / stream sum |

**Result:** Both implementations provide the eight reports. The AI version improves balance sorting by preserving accounts with equal balances and different IDs.

## Build and Test

The project targets **Java 8**. Use JDK 17+ for Maven and formatting.

```sh
mvn test
mvn spotless:apply
mvn spotless:check
```

Source files are in `src/` and tests in `test/`.

## Run the Application

Run `com.luxoft.bankapp.main.BankApplication` in IntelliJ IDEA.

- No arguments: starts the banking demo.
- `-statistics`: opens the reporting console.
- `display statistic`: displays all 8 reports.
- `exit`: closes the console.

The sample contains **3 clients, 4 accounts, a total balance of 225.00, and used overdraft of 15.00**.
