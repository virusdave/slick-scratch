package virusdave.db

import slick.codegen.SourceCodeGenerator

// TODO(dave): I can't remember why i didn't just use the slick codegen main.  Perhaps because of the amount
// of typing it required?  Maybe it should be removed...
object TablesCodeGen {
  def main(args: Array[String]): Unit = {
    SourceCodeGenerator.main(args)
  }
}
