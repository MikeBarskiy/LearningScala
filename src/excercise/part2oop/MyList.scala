package excercise.part2oop

abstract class MyList[+A] {

  /*
    head = first of element of the list
    tail = remainder of the list
    isEmpty = is this list empty
    add(int) => new list with added element
    toString => a string representation of the list
 */

  def head(): A

  def tail(): MyList[A]

  def isEmpty: Boolean

  def add[B >: A](element: B): MyList[B]

  def printElements(): String

  override def toString: String = "[" + printElements() + "]"

  def map[B](transformer: A => B): MyList[B]

  def flatMap[B](transformer: A => MyList[B]): MyList[B]

  def filter(predicate: A => Boolean): MyList[A]

  // concatenation
  def ++[B >: A](list: MyList[B]): MyList[B]

  // hofs
  def foreach(f: A => Unit): Unit

  def sort(compare: (A, A) => Int): MyList[A]

  def zipWith[B, C](list: MyList[B], zip: (A, B) => C): MyList[C]
}

case object EmptyList extends MyList[Nothing] {
  override def head(): Nothing = throw new NoSuchElementException

  override def tail(): MyList[Nothing] = throw new NoSuchElementException

  override def isEmpty: Boolean = true

  override def add[B >: Nothing](element: B): MyList[B] = new ConsList(element, this)

  def printElements(): String = ""

  override def map[B](transformer: Nothing => B): MyList[B] = EmptyList

  override def flatMap[B](transformer: Nothing => MyList[B]): MyList[B] = EmptyList

  override def filter(predicate: Nothing => Boolean): MyList[Nothing] = EmptyList

  override def ++[B >: Nothing](list: MyList[B]): MyList[B] = list

  // hofs
  override def foreach(f: Nothing => Unit): Unit = ()

  override def sort(compare: (Nothing, Nothing) => Int): EmptyList.type = EmptyList

  override def zipWith[B, C](list: MyList[B], zip: (Nothing, B) => C): MyList[C] = {
    if (!list.isEmpty) throw new RuntimeException("Lists do not have the same length")
    else EmptyList
  }
}

case class ConsList[+A](h: A, t: MyList[A]) extends MyList[A] {
  override def head(): A = h

  override def tail(): MyList[A] = t

  override def isEmpty: Boolean = false

  override def add[B >: A](element: B): MyList[B] = new ConsList(element, this)

  def printElements(): String =
    if (t.isEmpty) "" + h
    else h + " " + t.printElements

  // higher-order functions: receive function or return other function as result
  override def map[B](transformer: A => B): MyList[B] = {
    new ConsList(transformer(h), t.map(transformer))
  }

  override def flatMap[B](transformer: A => MyList[B]): MyList[B] = {
    transformer(h) ++ t.flatMap(transformer)
  }

  override def filter(predicate: A => Boolean): MyList[A] = {
    if (predicate(h)) new ConsList(h, t.filter(predicate))
    else t.filter(predicate)
  }

  override def ++[B >: A](list: MyList[B]): MyList[B] = new ConsList[B](h, t ++ list)

  // hofs
  override def foreach(f: A => Unit): Unit = {
    f(h)
    t.foreach(f)
  }

  override def sort(compare: (A, A) => Int): MyList[A] = {
    def insert(x: A, sortedList: MyList[A]): MyList[A] = {
      if (sortedList.isEmpty) new ConsList(x, EmptyList)
      else if (compare(x, sortedList.head()) <= 0) new ConsList(x, sortedList)
      else new ConsList(sortedList.head(), insert(x, sortedList.tail()))
    }

    val sortedTail = t.sort(compare)
    insert(h, sortedTail)
  }

  override def zipWith[B, C](list: MyList[B], zip: (A, B) => C): MyList[C] = {

  }
}

object ConsList {

}

object ListTests extends App {
  val listOfIntegers: MyList[Int] = new ConsList(1, new ConsList(2, new ConsList(3, EmptyList)))
  val listOfStrings: MyList[String] = new ConsList("Hello", new ConsList("Scala", EmptyList))

  println(listOfIntegers) // [1 2 3]
  println(listOfStrings) // [Hello Scala]

  println(listOfIntegers.map(_ + 2).toString)

  println(listOfIntegers.filter(_ % 2 == 0).toString)

  val anotherListOfIntegers: MyList[Int] = new ConsList(6, new ConsList(4, new ConsList(5, EmptyList)))
  println(listOfIntegers ++ anotherListOfIntegers)
  println(listOfIntegers.flatMap((element: Int) => new ConsList(element, new ConsList(element + 1, EmptyList))).toString)

  listOfIntegers.foreach(println)
  println(listOfIntegers.sort((x, y) => y - x))
}