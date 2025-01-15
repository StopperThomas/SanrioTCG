package at.ac.fhstp.sanriotcg.model

data class Challenge(
    val name: String,
    val target: Int,
    var progress: Int = 0,
    val reward: Int,
    var claimed: Boolean = false
)