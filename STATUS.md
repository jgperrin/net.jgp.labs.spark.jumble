# Status of the challenge

## What works
* Finding the first part of the challenge, the single words, work.
* Single words are displayed in order of popularity to solve the challenge. I have tested against two new online puzzles. They are added to the repository as `puzzle6` and `puzzle7`.
* A puzzle is defined as a YAML file that is parsed at the beginning.

## What does not work/work as well as it could
 * Phase 2 is very slow.
 * I did not manage to run phase 2 on every puzzle, too much resources, too slow... (see enhancements below).
 * Phase 3 is suppose to reconcile the best final result against the choice of words for phase 1. It does not work the way I want for now.

## Running the app
* The application starts with: JumbleSolverApp.
* You can run one game at a time or chain them in an array (around line 65).
* Logging is enabled using log4j.
* You can set it to 
  - `INFO` to just get the result of the game.
  - `DEBUG` to get information about what is going on, as well as timing information.
  - `TRACE` to finely print detailed information, including content of dataframes.
 
## Tweaking
* To get more or less results you can tweak two "hyperparameters" in `K`:
  - `MAX_RESULT` limits the number of results per query on anagrams, as those can become really crazy. Queries are sorted by popularity anyway.
  - `MAX_SOLUTION_FINAL_CLUE` limits the number of solutions for the final clue.

## What can be enhanced
* Performance.	 
  - It's very **slow**. I did not get a chance to try the app on [CLEGO](http://jgp.net/2017/09/22/new-dimension-apache-spark-clusters/).
  - I use a couple of `collect()` that are bad practice, but I did not find a quick way to replace them by (potentially) `map()`or flatMap()`.
* Memory.
  - Because of the `collect()`, you may have to tweak the heap space.
* The algorithm uses a `UNION`, it would be nice to replace it with one query...

## Other files
* `freq_dict_puzzle6.json` is a limited dataset of words to be used for puzzle6. As it is limited, it goes faster... 
