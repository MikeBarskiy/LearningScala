package demo_filesystem.commands

import demo_filesystem.files.DirEntry
import demo_filesystem.filesystem.State

class Ls extends Command {

  override def apply(state: State): State = {
    val contents = state.wd.contents
    val getContents = createNiceOutput(contents)
    state.setMessage(getContents)
  }

  def createNiceOutput(contents: List[DirEntry]): String = {
    if (contents.isEmpty) ""
    else {
      val entry = contents.head
      entry.name + "[" + entry.getType + "]\n" + createNiceOutput(contents.tail)
    }
  }
}
