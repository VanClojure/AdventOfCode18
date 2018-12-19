# Hitting the wall
The error was that function `next-game-state` expected a matrix, but it returned a `seq` of `seq` -  result of `partition` (which was fed by `let`, which also creates a `seq`). The problem only showed up during the second iteration.

Hiding the problem was our use of `get-in`. While being helpful, `get-in` returns `nil` for non-existing indices, or for structures other than a map or a vector (in our case it was a seq of seq), or even for `nil`!

```
(get-in [0 1 2] [:non-existing-index-or-key]) ;=> nil!
(get-in nil nil) ;=> nil and quiet!
```

We meant for our overall structure of states to be a matrix. Then the following would work:

```
(get-in [[:| :#]
         [:| :.]]
        [0 1]) ;=> :#
```

But after iteration, our overall structure of states (in `loop`'s parameter 'g') was not a matrix anymore. It became `seq` of `seq` instead, hence `get-in` bit us silently:

```
(get-in '((:| :#)
          (:| :.))
         [0 1]) ;=> nil!
```

Locating it took a while, fix was easy: https://github.com/VanClojure/AdventOfCode18/commit/435b78f.

Side note: It wouldn't be trivial to prevent it by more testing either. Why? Because the most natural way is to test equality by `=`, but `=` considers vectors and `seq` (with equal items) equal. Hence we would need an extra check of `(type ...)` or (vector? ...)`.

Do you know a gut feeling telling you something is wrong? A hint to the problem existed even during the first iteration: function `next-game-state` required `:while (some? cell)`. That didn't make sense, as all cells were initially in a non-`nil` state. Hence, if the execution forces you to do something that seemed strange, dig deeper.

# Retrospective

Once we agree on function signatures, how about splitting their implementation and writing tests between various people. That avoids bias. It gets the test to the implementor early (hence faster feedback for the implementor, and writing and testing integration early).

## Glossary
That would have helped with assumptions and spreading the knowledge.

## Examples of assumptions
We agreed that the cells ("types") would be keywords, but the providers only assumed them to be `:|, :#` and `:.`

## Spreading the knowledge, yet staying in the flow
After our initial delegation (main functions), we added some helper functions: `char->keyword` and `cell`. However, the others didn't know, hence couldn't reuse them. One way to keep up to date could be: more frequent GIT push and continuously piping `grep defn` into some dashboard. That would need function signatures to take one line each (is that common?), or a more advanced filter (for functions with multiple arities).

## Coding style guide
Is it worth? Any existing or custom?

## Design
I love how Andrea made function `next-game-state` flexible to integrate by having a function as a parameter `neighbour-provider`. It makes it easy to test. That made it plug-and-play instead of plug-and-pray.
