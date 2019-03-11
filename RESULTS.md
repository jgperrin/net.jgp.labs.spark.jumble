# Results

Timings in this file are not reflective of reality as many were run in parallel.

## Puzzle 1

Using:
*  `MAX_RESULT` set to 25.

```
2019-03-11 08:23:23.844 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:169): Now playing: Puzzle 1
2019-03-11 08:23:25.601 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:207): Tentative solutions for phase 1:
2019-03-11 08:23:33.188 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:351): Word #1: gland
2019-03-11 08:23:34.258 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:351): Word #2: major, joram, jarmo
2019-03-11 08:23:35.217 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:351): Word #3: becalm
2019-03-11 08:23:36.221 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:351): Word #4: lawyer, warely, yawler
2019-03-11 09:22:18.777 -ERROR --- [er-event-loop-4] Logging$class.logError(Logging.scala:70): Lost executor driver on localhost: Executor heartbeat timed out after 1204616 ms
```

Using:
*  `MAX_RESULT` set to 50.

```
2019-03-11 06:35:09.252 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:169): Now playing: Puzzle 1
2019-03-11 06:35:10.663 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:207): Tentative solutions for phase 1:
2019-03-11 06:35:17.203 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:351): Word #1: gland
2019-03-11 06:35:18.826 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:351): Word #2: major, joram, jarmo
2019-03-11 06:35:19.548 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:351): Word #3: becalm
2019-03-11 06:35:20.595 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:351): Word #4: lawyer, warely, yawler
2019-03-11 07:49:10.609 -ERROR --- [ver-heartbeater] Logging$class.logError(Logging.scala:91): Uncaught exception in thread driver-heartbeater
java.lang.OutOfMemoryError: GC overhead limit exceeded
2019-03-11 07:49:10.613 -ERROR --- [r for task 2596] Logging$class.logError(Logging.scala:91): Exception in task 3.0 in stage 319.0 (TID 2596)
java.lang.OutOfMemoryError: GC overhead limit exceeded
```


## Puzzle 2
Using:
*  `MAX_RESULT` set to 50.

```
2019-03-11 05:26:10.754 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:167): Now playing: Puzzle 2
2019-03-11 05:26:12.005 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:205): Tentative solutions for phase 1:
2019-03-11 05:26:18.967 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #1: blend
2019-03-11 05:26:20.244 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #2: avoid
2019-03-11 05:26:21.423 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #3: sychee, cheesy
2019-03-11 05:26:22.591 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #4: camera, mareca, acream
2019-03-11 05:32:37.607 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:278): Solutions for final clue (phase 2):
2019-03-11 05:32:53.360 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #1/8449: air ebay add
2019-03-11 05:32:53.360 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #2/8449: add ebay air
2019-03-11 05:32:53.360 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #3/8449: are ibad day
2019-03-11 05:32:53.361 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #4/8449: are adib day
2019-03-11 05:32:53.361 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #5/8449: day ibad are
2019-03-11 05:32:53.361 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #6/8449: day adib are
2019-03-11 05:32:53.361 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #7/8449: bid area day
2019-03-11 05:32:53.361 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #8/8449: day area bid
2019-03-11 05:32:53.361 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #9/8449: are yaba did
2019-03-11 05:32:53.362 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #10/8449: are baya did
2019-03-11 05:32:53.362 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #11/8449: are abay did
2019-03-11 05:32:53.363 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #12/8449: did yaba are
2019-03-11 05:32:53.363 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #13/8449: did baya are
2019-03-11 05:32:53.363 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #14/8449: did abay are
2019-03-11 05:32:53.363 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #15/8449: did area bay
2019-03-11 05:32:53.363 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:324): Limiting out put to first 15 solutions.
2019-03-11 05:32:53.461 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:294): Solutions for phase 1:
2019-03-11 05:33:41.555 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #1: blend
2019-03-11 05:34:06.653 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #2: avoid
2019-03-11 05:34:29.630 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #3: sychee, cheesy
2019-03-11 05:34:53.773 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #4: camera, mareca, acream
2019-03-11 05:34:53.773 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:299): Game played in 523017 ms.
```

## Puzzle 3
Using:
*  `MAX_RESULT` set to 50.

```
```

## Puzzle 4
Using:
*  `MAX_RESULT` set to 50.

```
2019-03-11 05:39:11.044 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:167): Now playing: Puzzle 4
2019-03-11 05:39:13.118 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:205): Tentative solutions for phase 1:
2019-03-11 05:39:21.165 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #1: dinky
2019-03-11 05:39:22.141 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #2: agiel, galei, agile
2019-03-11 05:39:22.925 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #3: encore
2019-03-11 05:39:23.629 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #4: devout
2019-03-11 05:39:31.005 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:278): Solutions for final clue (phase 2):
2019-03-11 05:39:31.809 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #1/2: addition
2019-03-11 05:39:31.809 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Solution #2/2: toddling
2019-03-11 05:39:31.902 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:294): Solutions for phase 1:
2019-03-11 05:39:34.541 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #1: dinky
2019-03-11 05:39:36.129 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #2: agiel, galei, agile
2019-03-11 05:39:37.108 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #3: encore
2019-03-11 05:39:38.051 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #4: devout
2019-03-11 05:39:38.051 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:299): Game played in 27001 ms.
```

## Puzzle 5
Using:
*  `MAX_RESULT` set to 50.

```
2019-03-11 05:59:51.075 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:167): Now playing: Puzzle 5
2019-03-11 05:59:52.771 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:205): Tentative solutions for phase 1:
2019-03-11 05:59:59.130 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #1: trying, trigyn, tyring
2019-03-11 06:00:00.410 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #2: divert
2019-03-11 06:00:01.397 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #3: seaman
2019-03-11 06:00:02.397 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #4: deceit
2019-03-11 06:00:03.295 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #5: shadow
2019-03-11 06:00:04.217 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:349): Word #6: kechel, heckle
Exception in thread "main" java.lang.OutOfMemoryError: GC overhead limit exceeded
	at scala.collection.immutable.HashSet$HashTrieSet.updated0(HashSet.scala:552)
	at scala.collection.immutable.HashSet$HashTrieSet.updated0(HashSet.scala:543)
	at scala.collection.immutable.HashSet$HashTrieSet.updated0(HashSet.scala:543)
```

## Puzzle 6
Using:
*  `MAX_RESULT` set to 50.
* The full word dataset.

```
2019-03-11 04:58:54.928 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:165): Now playing: Puzzle 6
2019-03-11 04:58:55.717 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:203): Tentative solutions for phase 1:
2019-03-11 04:59:00.690 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:347): Word #1: venue
2019-03-11 04:59:01.514 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:347): Word #2: tepid, depit
2019-03-11 04:59:02.222 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:347): Word #3: weakly
2019-03-11 04:59:02.899 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:347): Word #4: arghan, harang, hangar
2019-03-11 05:01:31.093 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:276): Solutions for final clue (phase 2):
2019-03-11 05:01:43.350 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #1/4468: paved the way
2019-03-11 05:01:43.350 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #2/4468: paved way the
2019-03-11 05:01:43.350 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #3/4468: waved the pay
2019-03-11 05:01:43.350 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #4/4468: waved pay the
2019-03-11 05:01:43.350 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #5/4468: wavey the pda
2019-03-11 05:01:43.350 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #6/4468: wavey pda the
2019-03-11 05:01:43.350 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #7/4468: wavey the pad
2019-03-11 05:01:43.350 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #8/4468: wavey pad the
2019-03-11 05:01:43.351 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #9/4468: wendy the pav
2019-03-11 05:01:43.352 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #10/4468: wendy pav the
2019-03-11 05:01:43.352 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #11/4468: peavy the wad
2019-03-11 05:01:43.352 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #12/4468: peavy the daw
2019-03-11 05:01:43.353 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #13/4468: peavy the awd
2019-03-11 05:01:43.353 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #14/4468: peavy awd the
2019-03-11 05:01:43.353 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:320): Solution #15/4468: peavy daw the
2019-03-11 05:01:43.353 - INFO --- [           main] ettyPrintFinal(JumbleSolverApp.java:322): Limiting out put to first 15 solutions.
2019-03-11 05:01:43.527 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:292): Solutions for phase 1:
2019-03-11 05:02:33.788 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:347): Word #1: venue
2019-03-11 05:03:02.979 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:347): Word #2: tepid, depit
2019-03-11 05:03:31.541 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:347): Word #3: weakly
2019-03-11 05:03:59.945 - INFO --- [           main] pp.prettyPrint(JumbleSolverApp.java:347): Word #4: arghan, harang, hangar
2019-03-11 05:03:59.945 - INFO --- [           main] SolverApp.play(JumbleSolverApp.java:297): Game played in 305016 ms.
```