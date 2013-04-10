package models

import scala.collection.immutable

import com.shorrockin.cascal.utils.Conversions._
import com.shorrockin.cascal.model.{Column, StandardKey}

object DaoHelper {

  // NOTE: values won't be in the same order as ColumnPredicate, moreover, _.name and _.value can be read only once
  def mapColumns(values: Seq[Column[StandardKey]]): immutable.Map[String,String] = values.map(v => (string(v.name).trim -> string(v.value).trim)).toMap

}
