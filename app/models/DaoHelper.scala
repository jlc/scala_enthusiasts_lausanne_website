package models

import com.shorrockin.cascal.utils.Conversions._
import com.shorrockin.cascal.model.{Column, StandardKey}

object DaoHelper {

  // NOTE: values won't be in the same order as ColumnPredicate, moreover, _.name and _.value can be read only once
  def mapColumns(values: Seq[Column[StandardKey]]) = values.map(v => (string(v.name) -> string(v.value))).toMap

}
