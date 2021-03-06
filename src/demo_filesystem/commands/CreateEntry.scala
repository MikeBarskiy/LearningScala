package demo_filesystem.commands

import demo_filesystem.files.{DirEntry, Directory}
import demo_filesystem.filesystem.State

abstract class CreateEntry(name: String) extends Command {

  override def apply(state: State): State = {
    val wd = state.wd
    if (wd.hasEntry(name)) {
      state.setMessage("Entry " + name + " already exists!")
    } else if (name.contains(Directory.SEPARATOR)) {
      state.setMessage(name + " must not contain separators!")
    } else if (checkIllegal(name)) {
      state.setMessage(name + ": illegal entry name!")
    } else {
      doCreateEntry(state, name)
    }
  }

  def checkIllegal(str: String): Boolean = {
    name.contains(".")
  }

  def doCreateEntry(state: State, name: String): State = {
    def updateStructure(currentDirectory: Directory, path: List[String], newEntry: DirEntry): Directory = {
      if (path.isEmpty) currentDirectory.addEntry(newEntry)
      else {
        val oldEntry = currentDirectory.findEntry(path.head).asDirectory
        currentDirectory.replaceEntry(oldEntry.name, updateStructure(oldEntry, path.tail, newEntry))
      }
    }

    val wd = state.wd

    // 1. All the directories in the full path
    val allDirsInPath = wd.getAllFoldersInPath

    // 2. Create new directory in the the working directory(wd)
    val newEntry: DirEntry = createSpecificEntry(state)

    // 3. Update the whole directory structure starting from the root (the directory structure is IMMUTABLE)
    val newRoot = updateStructure(state.root, allDirsInPath, newEntry)

    // 4. Find new working directory instance given wd's full path in the new directory structure
    val newWd = newRoot.findDescedant(allDirsInPath)

    State(newRoot, newWd)
  }

  def createSpecificEntry(state: State): DirEntry
}
