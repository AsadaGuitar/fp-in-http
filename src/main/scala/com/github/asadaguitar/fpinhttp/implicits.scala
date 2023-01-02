package com.github.asadaguitar.fpinhttp

object implicits:

    extension [A, B](xs: Either[A, B])
        @throws[RuntimeException]
        def unsafeGetValue: B = xs match
            case Left(_) => throw RuntimeException("Either.left is not an allowed value.")
            case Right(value) => value
        
