## Java 201: Assignment 1

#### NOTES (specifics):
* Expects a file passed in as a command line parameter.
* If there is a duplicate question in the same category the parser throws an error.
* Multiline check: Only checks for 2 line questions during parsing.
* I left the whitespace arbitrary so "What is Los Angeles" is the same as "What    is     Los    Angeles" (multiple spaces between words)
* Assumes no duplicate team names.
* There is a default name for every team in case the name is left empty.
* Ignores grammar so the answer needs to be posed as a question regardless of proper grammar.
* During Final Jeopardy you can't bet 0.
* Methods that are not used within the main method assume proper usage and use within the methods called in main, so they don't throw exceptions for missuse.