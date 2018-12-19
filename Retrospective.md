# Hitting the wall
The error only showed up during the second iteration.
Hiding the problem was our use of get-in. While being helpful, get-in returns nil for non-existing indices, or for structures other than a map or vector (in our case it was a seq of seq), or even for nil!

```
(get-in '((:| :#)(:| :.)) [0 1]) ;=> nil!
(get-in nil nil) ;=> nil!!!
```

We meant
`(get-in [[:| :#][:| :.]] [0 1]) ;=> :#`
but after iteration, our overall sructure of states 'g' was not a matrix, but a seq of seq:
`(get-in [[:| :#][:| :.]] [0 1]) ;=> nil!`

Digging it took a while, fix was easy: https://github.com/VanClojure/AdventOfCode18/commit/435b78f.

A hint to the problem existed even during the first iteration: function `next-game-state` required `:while (some? cell)`, which didn't make sense, as all cells were initially in a non-`nil` state.

# Retrospective

Once we agree on function signatures, how about splitting their implementation and writing tests between various people. That avoids bias. It gets the test to the implementor early (hence faster feedback for the implementor, and writing and testing integration early).

## Glossary
That would have helped with assumptions and spreading the knowledge.

## Examples of assumptions
We agreed that the cells ("types") would be keywords, but the providers only assumed them to be `:|, :#` and `:.`

## Spreading the knowledge, yet staying in the flow
After our initial delegation (main functions), we added some helper functions: `char->keyword` and `cell`. However, the others didn't know, hence didn't reuse them. One way to keep up to date could be: more frequent GIT push and continuously piping `grep defn` into some dashboard. That would need function signatures to take one line each (is that common?), or a more advanced filter (for functions with multiple arities).

## Coding style guide
Is it worth? Any existing or custom?

## Design
I love how Andrea made function `next-game-state` flexible to integrate by having a function as a parameter 'neighbour-provider'. It makes it easy to test. Plug-and-don't-pray.
