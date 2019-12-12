# QuizTest
Natera quiz autotests

Errors found so far:
- empty separator can be used
- max stored triangles is 11 instead of 10
- more than three values could be supplied in "input" string
- deleting non-existent triangle results in 200 OK instead of 404
- degenerate triangles creation is allowed
- digits with "f" postfix are accepted for side values
- digits with "d" postfix are accepted for side values
- "Infinity" is accepted for side value
- following symbols are not accepted as separator
  - // - Internal Server Error
  - ( - Internal Server Error
  - ) - Internal Server Error
  - [ - Internal Server Error
  - + - Internal Server Error
  - * - Internal Server Error
  - $
  - ^
  - № - Internal Server Error
  - ? - Internal Server Error
- application could return 0 as an area value
- application could return Infinity as an area value
- application could return Infinity as an perimeter value
- sending ~10 000 separated values crashes application (OOM)
