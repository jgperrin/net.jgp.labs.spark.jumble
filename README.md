#Exercise 2: Spark

The jumble puzzle is a common newspaper puzzle, it contains a series of anagrams that must be solved (see [Wikipedia](https://en.wikipedia.org/wiki/Jumble)). To solve, one must solve each of the individual jumbles. The circled letters are then used to create an additional anagram to be solved. In especially difficult versions, some of the anagrams in the first set can possess multiple solutions. To get the final answer, it is important to know all possible anagrams of a given series of letters.

You can play the game [online](https://www.jumble.com/playjumble).

Your challenge is to solve the five Jumble puzzles using Spark with Scala or Python, where it makes sense to do so. If the final puzzle has multiple possible answers, you are to include an algorithm to determine the most likely one. We have provided a dictionary where the "most common" English words are scored (1=most frequent, 9887=least frequent, 0=not scored due to infrequency of use). For each puzzle, produce the "most likely" (as you determine it) final anagram produced from solving all the other anagrams.

**Important Notes**: Part of your task is to have this be as production ready as possible - while there are only five puzzles now, assume that there could be many more, so use Spark in the most useful way, however you don't need to spend a lot of time on tweaking the parallelization parameters. The code should be deployable and maintainable as well. Don't spend more than 24 hours to complete as much of the assignment as you can.

Also included:
* freq_dict - Keys are English Dictionary words to be used in your solving of the jumbles. Non-zero values are the frequency rankings (1=most frequent). Zero values mean that the word is too infrequent to be ranked.
* Pictures of the jumbles we provided for you to solve - You can put these in whatever data format you'd like for your program to read in.

Please include the following in your repository:
* Your initial data (from the jumble pictures given)
* Output from your code