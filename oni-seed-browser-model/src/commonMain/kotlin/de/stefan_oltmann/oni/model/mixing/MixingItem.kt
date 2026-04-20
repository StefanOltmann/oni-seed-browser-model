/*
 * ONI Seed Browser
 * Copyright (C) 2026 Stefan Oltmann
 * https://stefan-oltmann.de
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.stefan_oltmann.oni.model.mixing

import de.stefan_oltmann.oni.model.AsteroidType
import de.stefan_oltmann.oni.model.Dlc
import de.stefan_oltmann.oni.model.ZoneType

sealed class MixingItem private constructor() {

    sealed class DlcMixingItem(
        val dlc: Dlc
    ) : MixingItem()

    sealed class AsteroidMixingItem(
        val asteroidType: AsteroidType
    ) : MixingItem()

    sealed class ZoneTypeMixingItem(
        val zoneType: ZoneType
    ) : MixingItem()

    fun getLevel(coordinateValue: Int): MixingLevel =
        MixingLevel.entries.getOrElse(coordinateValue) { MixingLevel.DISABLED }

    /* Frosty Planet Pack */
    data object DlcFrostyPlanet : DlcMixingItem(Dlc.FrostyPlanet)
    data object IceCaves : ZoneTypeMixingItem(ZoneType.IceCaves)
    data object CarrotQuarry : ZoneTypeMixingItem(ZoneType.CarrotQuarry)
    data object SugarWoods : ZoneTypeMixingItem(ZoneType.SugarWoods)
    data object CeresAsteroid : AsteroidMixingItem(AsteroidType.MixingCeresAsteroid)

    /* Bionic Booster Pack */
    data object DlcBionicBooster : DlcMixingItem(Dlc.BionicBooster)

    /* Prehistoric Planet Pack */
    data object DlcPrehistoricPlanet : DlcMixingItem(Dlc.PrehistoricPlanet)
    data object Garden : ZoneTypeMixingItem(ZoneType.PrehistoricGarden)
    data object Raptor : ZoneTypeMixingItem(ZoneType.PrehistoricRaptor)
    data object Wetlands : ZoneTypeMixingItem(ZoneType.PrehistoricWetlands)
    data object PrehistoricAsteroid : AsteroidMixingItem(AsteroidType.MixingPrehistoricAsteroid)

    companion object {

        val entries: List<MixingItem> = listOf(

            /* Frosty Planet Pack */
            DlcFrostyPlanet,
            IceCaves,
            CarrotQuarry,
            SugarWoods,
            CeresAsteroid,

            /* Bionic Booster Pack */
            DlcBionicBooster,

            /* Prehistoric Planet Pack */
            DlcPrehistoricPlanet,
            Garden,
            Raptor,
            Wetlands,
            PrehistoricAsteroid
        )
    }
}
