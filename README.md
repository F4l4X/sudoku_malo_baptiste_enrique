## sbt project compiled with Scala 3 : Malo LE CORVEC ; Baptiste KEUNEBROEK ; Enrique CARRETERO ; Gaetan MOUNSAMY


### Usage

This is a normal sbt project. You can compile code with `sbt compile`, run it with `sbt run`, and `sbt console` will start a Scala 3 REPL.

For more information on the sbt-dotty plugin, see the
[scala3-example-project](https://github.com/scala/scala3-example-project/blob/main/README.md).


### How to run
    . Open the SBT Shell by typing "sbt" in your terminal. (Or, if you're using Jetbrains, you'll find the "sbt shell" tab in your terminal.)
    . When shell initialization is complete, enter "run".
    . once the program is ready, it will ask you to enter the Path of the json file containing the sudoku to be solved.
    . Once the job is done, the program displays in the shell the sudoku you entered, followed by the solved sudoku.
    . in the data_test -> result folder, you'll find a Json file that also contains the solved sudoku.

### Json format example

    "RawData":
        [
            [0,9,0,8,6,5,2,0,0],
            [0,0,5,0,1,2,0,6,8],
            [0,0,0,0,0,0,0,4,0],
            [0,0,0,0,0,8,0,5,6],
            [0,0,8,0,0,0,4,0,0],
            [4,5,0,9,0,0,0,0,0],
            [0,8,0,0,0,0,0,0,0],
            [2,4,0,1,7,0,5,0,0],
            [0,0,7,2,8,3,0,9,0]
        ]
The 0s represent the boxes to be filled in