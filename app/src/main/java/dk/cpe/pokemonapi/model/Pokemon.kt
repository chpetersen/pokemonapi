package dk.cpe.pokemonapi.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.json.JSONObject

@JsonClass(generateAdapter = true)
data class Pokemon(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "base_experience") val baseExperience: Int,
    @Json(name = "height") val height: Int,
    @Json(name = "is_default") val isDefault: Boolean,
    @Json(name = "order") val order: Int,
    @Json(name = "weight") val weight: Int,
    @Json(name = "types") val types: List<PokemonType>,
    @Json(name = "sprites") val sprites: PokemonSprites
) {
    fun findBestArtWork(): String? {
        return if (sprites.officialArtwork != null) {
            sprites.officialArtwork.officialArtwork?.frontDefault
        } else {
            sprites.frontDefault
        }
    }

    fun nameTitleCase() = name.replaceFirstChar { it.titlecase() }
}

@JsonClass(generateAdapter = true)
data class PokemonSprites(
    @Json(name = "back_default") val backDefault: String?,
    @Json(name = "back_shiny") val backShiny: String?,
    @Json(name = "front_default") val frontDefault: String?,
    @Json(name = "front_shiny") val frontShiny: String?,
    @Json(name = "back_female") val backFemale: String?,
    @Json(name = "back_shiny_female") val backShinyFemale: String?,
    @Json(name = "front_female") val frontFemale: String?,
    @Json(name = "front_shiny_female") val frontShinyFemale: String?,
    @Json(name = "other") val officialArtwork: OfficialArtwork?
)

@JsonClass(generateAdapter = true)
data class OtherSprites(
    @Json(name = "front_default") val frontDefault: String?
)


@JsonClass(generateAdapter = true)
data class OfficialArtwork(
    @Json(name = "official-artwork") val officialArtwork: OtherSprites?
)

@JsonClass(generateAdapter = true)
data class PokemonType(
    @Json(name = "slot") val slot: Int,
    @Json(name = "type") val type: ApiResult
)