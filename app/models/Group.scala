package models

abstract class Group(val ty: Int)

object Group {
  case class Anonymous() extends Group(0)
  case class User() extends Group(1)
  case class God() extends Group(2)

  def fromName(grp: String): Group = grp.trim.toLowerCase match {
    case "user" => User()
    case "god" => God()
    case _ => Anonymous()
  }
}

