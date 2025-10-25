/*
 * ONI Seed Browser
 * Copyright (C) 2025 Stefan Oltmann
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
package de.stefan_oltmann.oni.model.server

import de.stefan_oltmann.oni.model.ClusterType
import de.stefan_oltmann.oni.model.server.upload.UploadCluster
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * This is the format expected to be sent from the mod
 */
@Serializable
data class Upload(

    val userId: String,

    val installationId: String,

    val gameVersion: Int,

    val fileHashes: Map<String, String>,

    val cluster: UploadCluster

) {

    val uploadSteamId: String? = if (userId.startsWith("Steam-"))
        userId.drop(6)
    else
        null

    @OptIn(ExperimentalUuidApi::class)
    fun check(
        tokenSteamId: String,
        currentGameVersion: Int
    ): UploadCheckResult {

        /* InstallationId must be valid UUID */
        try {
            Uuid.parse(installationId)
        } catch (_: IllegalArgumentException) {
            return UploadCheckResult.Error("Invalid 'installationId': $installationId")
        }

        return when {

            /* InstallationId is mandatory */
            installationId.isBlank() ->
                UploadCheckResult.Error("Missing 'installationId'")

            /* UserId is mandatory */
            userId.isBlank() ->
                UploadCheckResult.Error("Missing 'userId'")

            /* We only accept the Steam version */
            uploadSteamId == null ->
                UploadCheckResult.Error("User ID was not Steam: $userId")

            /* Steam ID in upload must match Steam ID in token */
            uploadSteamId != tokenSteamId ->
                UploadCheckResult.Error("Steam ID mismatch: $uploadSteamId != $tokenSteamId")

            /* Game version must be set */
            gameVersion == 0 ->
                UploadCheckResult.Error("Missing 'gameVersion'")

            /* File hashes must be set */
            fileHashes.isEmpty() ->
                UploadCheckResult.Error("Missing 'fileHashes'")

            /* ModHash must be set */
            fileHashes["modHash"].isNullOrBlank() ->
                UploadCheckResult.Error("Missing 'fileHashes.modHash'")

            /* Coordinate must be set */
            cluster.coordinate.isBlank() ->
                UploadCheckResult.Error("Missing 'cluster.coordinate'")

            /* Coordinate must be valid */
            !ClusterType.isValidCoordinate(cluster.coordinate) ->
                UploadCheckResult.Error("Invalid 'cluster.coordinate': ${cluster.coordinate}")

            /* Cluster must have asteroids */
            cluster.asteroids.isEmpty() ->
                UploadCheckResult.Error("Missing 'cluster.asteroids'")

            /* Must be the current version of the game */
            cluster.gameVersion < currentGameVersion ->
                UploadCheckResult.Error("Game version too old: ${cluster.gameVersion} < $currentGameVersion")

            /*
             * If none of the issues above are present, the upload is accepted.
             */
            else -> UploadCheckResult.Okay
        }
    }
}
