[![Build Status](https://travis-ci.org/ascheja/rokkstar.svg?branch=master)](https://travis-ci.org/ascheja/rokkstar)

Rokkstar
========
This is a WIP parser/interpreter/transpiler for the [rockstar programming language](https://github.com/RockstarLang/rockstar),
written in Kotlin.

Project layout
--------------
- `ast`: abstract syntax tree classes
- `typesystem`: logic rockstar's dynamic types
- `parser` (uses `ast`): parser (and lexer) for building up an AST from source code
- `interpreter` (uses `ast` and `typesystem`): interpreter that traverses the AST
- `runner` (uses `interpreter` and `parser`): `main()` for commandline tool

Using the runner via gradle
---------------------------
```bash
# starts the api on port 8080 (`Listen to ...` will always receive empty strings)
./gradlew :runner:run --args='api 8080'

# transpiles the given rockstar source code file to javascript (without type conversions)
./gradlew :runner:run --args='transpile:js <file>'

# launches the interpreter on the given file (Will not work with `Listen to ...`, you have to run it manually in that case)
./gradlew :runner:run --args='run <file>'
```

Using the runner via the Docker image
-------------------------------------
```bash
# start the api on port 8080
docker run --rm -p 8080:8080 ascheja/rokkstar api 8080

# transpiles the given rockstar source code file (located in the current folder) to javascript (without type conversions)
docker run --rm -v $(dirname $(readlink -f .)):/mnt ascheja/rokkstar transpile:js /mnt/[file]

# launches the interpreter on the given file (located in the current folder)
docker run --rm -ti -v $(dirname $(readlink -f .)):/mnt ascheja/rokkstar run /mnt/[file]
```

Plans for the future
--------------------
- [x] Add more tests, enable github-ci
- [x] Add position tracking for tokens
- [ ] Improve error handling in Parser (try to recover from exceptions)
- [ ] Rockstar plugin for IntelliJ IDEA
- [x] Provide a Docker image
- [ ] Make Rokkstar runnable on all Kotlin platforms (jvm, javascript, kotlin-native) and/or [graal/substratevm](https://github.com/oracle/graal)
- [ ] Use llvm (via kotlin-native) to create a real compiler

Known Issues
------------
- Poor error messages
