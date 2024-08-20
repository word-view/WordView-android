/*
 * Copyright (c) 2024 Arthur Araujo
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package cc.wordview.app.subtitle

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Hearing
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RecordVoiceOver
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.ViewHeadline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import br.com.frazo.compose_resources.IconResource
import cc.wordview.app.R

var wordIcon: HashMap<String, Painter> = hashMapOf()

@SuppressLint("ComposableNaming")
@Composable
fun initializeIcons() {
    if (wordIcon.size <= 0) wordIcon = hashMapOf(
        "tear" to IconResource.fromImageVector(Icons.Outlined.WaterDrop).asPainterResource(),
        "words" to IconResource.fromImageVector(Icons.Outlined.ViewHeadline).asPainterResource(),
        "listen" to IconResource.fromImageVector(Icons.Outlined.Hearing).asPainterResource(),
        "cloud" to IconResource.fromImageVector(Icons.Outlined.Cloud).asPainterResource(),
        "voice" to IconResource.fromImageVector(Icons.Outlined.RecordVoiceOver).asPainterResource(),
        "rain" to IconResource.fromDrawableResource(R.drawable.rainy).asPainterResource(),
        "night" to IconResource.fromDrawableResource(R.drawable.night).asPainterResource(),
        "umbrella" to IconResource.fromImageVector(Icons.Outlined.BeachAccess).asPainterResource(),
        "street" to IconResource.fromDrawableResource(R.drawable.road).asPainterResource(),
        "road" to IconResource.fromDrawableResource(R.drawable.road).asPainterResource(),
        "star" to IconResource.fromImageVector(Icons.Outlined.Star).asPainterResource(),
        "two_people" to IconResource.fromImageVector(Icons.Outlined.People).asPainterResource(),
        "one_person" to IconResource.fromImageVector(Icons.Outlined.Person).asPainterResource(),
        "cant_see" to IconResource.fromImageVector(Icons.Outlined.VisibilityOff).asPainterResource(),
        "can_see" to IconResource.fromImageVector(Icons.Outlined.Visibility).asPainterResource(),
        "destroy" to IconResource.fromDrawableResource(R.drawable.destruction).asPainterResource(),
        "walk" to IconResource.fromImageVector(Icons.AutoMirrored.Outlined.DirectionsWalk).asPainterResource()
    )
}

fun getIconForWord(parent: String): Painter? {
    return wordIcon[parent]
}