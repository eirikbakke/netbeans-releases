<?php

interface FooInterface
{
   /**
    * @return Bar
    */
   public function someMethod(int $baz);
}

class Foo implements FooInterface
{
}
