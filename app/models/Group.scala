package models

abstract class Group(val ty: Int)

object Group {
  case class Anonymous() extends Group(0)
  case class User() extends Group(1)
  case class God() extends Group(2)
}

