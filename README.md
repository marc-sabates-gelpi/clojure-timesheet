# clojure-timesheet

Session manager
Sums up time, starts new session or ends last session

## Usage

    $ lein run "file_path" Adds the completed sessions
    $ lein run "file_path" "start" ["Description"] Starts a new session with an optional description (the default description is used otherwise)
    $ lein run "file_path" "ende" Ends the latest currently started session

## Todo

   * Cater for multiple started sessions when end op
   * Warn of multiple started sessions when start op