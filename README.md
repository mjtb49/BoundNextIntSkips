# BoundNextIntSkips
A small utility project to bound the number of times the condition in the do while loop of java.util.Random's nextInt method can return false in a large number of consecutive calls.

Currently, the algorithm misses sequences which cross over the 0 seed, but this is me being lazy. More concerning is how slowly everything runs if the argument passed to nextInt is even mildly large.

That being said, this algorithm has significantly more potential than just nextInt() skips, in theory you just need a set of extremely rare seeds and an analogue of this algorithm could find seeds where seeds of the set occur in much higher density than usual in subsequent calls.
