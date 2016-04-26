/**
 * This file is part of Todo.txt Touch, an Android app for managing your todo.txt file (http://todotxt.com).

 * Copyright (c) 2009-2012 Todo.txt contributors (http://todotxt.com)

 * LICENSE:

 * Todo.txt Touch is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 2 of the License, or (at your option) any
 * later version.

 * Todo.txt Touch is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.

 * You should have received a copy of the GNU General Public License along with Todo.txt Touch.  If not, see
 * //www.gnu.org/licenses/>.

 * @author Todo.txt contributors @yahoogroups.com>
 * *
 * @license http://www.gnu.org/licenses/gpl.html
 * *
 * @copyright 2009-2012 Todo.txt contributors (http://todotxt.com)
 */
package nl.mpcjanssen.simpletask

object Constants {

    const val DATE_FORMAT = "YYYY-MM-DD"

    const val BROADCAST_UPDATE_UI = "UPDATE_UI"
    const val BROADCAST_TODOLIST_CHANGED = "TODOLIST_CHANGED"
    const val BROADCAST_THEME_CHANGED = "THEME_CHANGED"
    const val BROADCAST_DATEBAR_SIZE_CHANGED = "DATEBAR_SIZE_CHANGED"

    const val BROADCAST_HIGHLIGHT_SELECTION = "HIGHLIGHT_SELECTION"

    // Sharing constants
    const val SHARE_FILE_NAME = "simpletask.txt"

    // Public intents
    const val INTENT_START_FILTER = "nl.mpcjanssen.simpletask.START_WITH_FILTER"
    const val INTENT_BACKGROUND_TASK = "nl.mpcjanssen.simpletask.BACKGROUND_TASK"


    // Intent extras
    const val EXTRA_BACKGROUND_TASK = "task"
    const val EXTRA_HELP_PAGE = "page"

    // Android OS specific constants
    const val ANDROID_EVENT = "vnd.android.cursor.item/event"


    // Help pages
    const val HELP_INDEX = "index.en.md"
    const val HELP_ADD_TASK = "addtask.en.md"

}

enum class DateType {
    DUE, THRESHOLD
}
