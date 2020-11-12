# BoundNextIntSkips
A small utility project to bound the number of times the condition in the do while loop of java.util.Random's nextInt method can return false in a large number of consecutive calls.

Everything runs very very slowly if the argument passed to nextInt is even mildly large. An alternative exists if you merely want to bound a modest number of calls, just brute force forward the maximum number of calls from every skipping call.

That being said, this algorithm has significantly more potential than just nextInt() skips, in theory you just need a set of extremely rare seeds and an analogue of this algorithm could find seeds where seeds of the set occur in much higher density than usual in subsequent calls.
