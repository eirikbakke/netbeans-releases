<?php

interface FooInterface
{
   public function someMethod(int $baz): Bar;
}

class Foo implements FooInterface
{
}
