package at.ac.fhstp.sanriotcg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import at.ac.fhstp.sanriotcg.data.AppDatabase
import at.ac.fhstp.sanriotcg.data.CoinBalanceDao
import at.ac.fhstp.sanriotcg.model.Album
import at.ac.fhstp.sanriotcg.model.Card
import at.ac.fhstp.sanriotcg.model.Challenge
import at.ac.fhstp.sanriotcg.model.Circle
import at.ac.fhstp.sanriotcg.model.CoinBalance
import at.ac.fhstp.sanriotcg.repository.AlbumRepository
import at.ac.fhstp.sanriotcg.repository.CardRepository
import at.ac.fhstp.sanriotcg.repository.ChallengeRepository
import at.ac.fhstp.sanriotcg.ui.theme.SanrioTCGTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    private lateinit var cardRepository: CardRepository
    private lateinit var albumRepository: AlbumRepository
    private lateinit var challengeRepository: ChallengeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appDatabase = AppDatabase.getDatabase(this)
        cardRepository = CardRepository(appDatabase.cardDao())
        albumRepository = AlbumRepository(appDatabase.albumDao())
        challengeRepository = ChallengeRepository(appDatabase.challengeDao())

        lifecycleScope.launch {
            val existingChallenges = challengeRepository.getChallenges().first()

            if (existingChallenges.isEmpty()) {
                val challenges = listOf(
                    Challenge(name = "Open 10 Packs", target = 10, reward = 500),
                    Challenge(name = "Collect 5 Rare Cards", target = 5, reward = 300),
                    Challenge(name = "Spend 500 Coins", target = 500, reward = 200),
                    Challenge(name = "Sell 5 Cards", target = 5, reward = 150)
                )

                challenges.forEach { challenge ->
                    challengeRepository.insertOrUpdate(challenge)
                }
            }
        }

        setContent {
            SanrioTCGTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CardApp(
                        cardRepository,
                        albumRepository,
                        challengeRepository,
                        coinBalanceDao = appDatabase.coinBalanceDao()
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Sanrio Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "SanrioTCG",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily(Font(R.font.pacifico)),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7687D3)
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFFDBF7))
    )
}


@Composable
fun AppFooter(navController: NavHostController) {
    NavigationBar(containerColor = Color(0xFFFFDBF7)) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF7687D3)) },
            label = { Text("Home", color = Color(0xFF7687D3)) },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Collection", tint = Color(0xFF7687D3)) },
            label = { Text("Collection", color = Color(0xFF7687D3)) },
            selected = false,
            onClick = { navController.navigate("collection") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Album", tint = Color(0xFF7687D3)) },
            label = { Text("Album", color = Color(0xFF7687D3)) },
            selected = false,
            onClick = { navController.navigate("album") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Storefront, contentDescription = "Card Shop", tint = Color(0xFF7687D3)) },
            label = { Text("Card Shop", color = Color(0xFF7687D3)) },
            selected = false,
            onClick = { navController.navigate("shop") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = "Info", tint = Color(0xFF7687D3)) },
            label = { Text("Info", color = Color(0xFF7687D3)) },
            selected = false,
            onClick = { navController.navigate("info") }
        )
    }
}


@Composable
fun CardApp(
    cardRepository: CardRepository,
    albumRepository: AlbumRepository,
    challengeRepository: ChallengeRepository,
    coinBalanceDao: CoinBalanceDao
) {
    val navController = rememberNavController()
    val collectedCards by cardRepository.allCards.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val coinBalanceFlow = coinBalanceDao.getCoinBalance()
    val coinBalanceState = coinBalanceFlow.collectAsState(initial = CoinBalance(balance = 500))
    val coinBalance = coinBalanceState.value?.balance ?: 500 // default fallback coinBalance
    val challengesState by challengeRepository.getChallenges().collectAsState(initial = emptyList())
    val totalSpent = remember { mutableIntStateOf(0) }

    fun updateCoinBalance(newBalance: Int) {
        coroutineScope.launch {
            coinBalanceDao.insertOrUpdate(CoinBalance(balance = newBalance))
        }
    }

    Scaffold(
        topBar = { AppHeader() },
        bottomBar = { AppFooter(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomePage(
                        challenges = challengesState,
                        coinBalance = coinBalance,
                        onCoinBalanceChange = { updateCoinBalance(it) },
                        challengeRepository = challengeRepository
                    )
                }
                composable("collection") {
                    CollectionPage(navController, collectedCards)
                }
                composable("album") { AlbumPage(collectedCards, albumRepository) }
                composable("shop") {
                    ShopPage(
                        navController,
                        coinBalance,
                        onCoinBalanceChange = { updateCoinBalance(it) },
                        onCardsAdded = { newCards ->
                            coroutineScope.launch {
                                newCards.forEach { card -> cardRepository.insert(card) }
                            }
                        },
                        challenges = challengesState,
                        totalSpent = totalSpent,
                        challengeRepository = challengeRepository
                    )
                }
                composable("info") { InfoPage() }
                composable("fullScreenCard/{cardId}") { backStackEntry ->
                    val cardId = backStackEntry.arguments?.getString("cardId")?.toInt() ?: 0
                    FullScreenCardPage(cardId, navController, cardRepository, onSell = { price ->
                        updateCoinBalance(coinBalance + price)

                        val sellChallenge = challengesState.find { it.name == "Sell 5 Cards" }
                        sellChallenge?.let {
                            val newProgress = it.progress + 1
                            it.progress = minOf(newProgress, it.target)

                            coroutineScope.launch {
                                challengeRepository.updateProgress(it.id, it.progress)
                            }
                        }
                    })
                }
                composable("packOpening/{cardIds}") { backStackEntry ->
                    val cardIdsString = backStackEntry.arguments?.getString("cardIds")
                    val cardIds = cardIdsString?.split(",")?.map { it.toInt() } ?: emptyList()
                    PackOpeningScreen(cardIds, navController)
                }
                composable("minigame") {
                    MinigamePage(
                        navController = navController,
                        onCoinBalanceChange = { earnedCoins ->
                            updateCoinBalance(coinBalance + earnedCoins)
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun HomePage(
    challenges: List<Challenge>,
    coinBalance: Int,
    challengeRepository: ChallengeRepository,
    onCoinBalanceChange: (Int) -> Unit
) {
    var showClaimDialog by remember { mutableStateOf(false) }

    fun calculateRewardCoins(): Int {
        return challenges.filter { it.progress >= it.target && !it.claimed }
            .sumOf { it.reward }
    }

    var rewardCoins by remember { mutableIntStateOf(0) }
    val hasUnclaimedRewards = challenges.any { it.progress >= it.target && !it.claimed }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF0FB))
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Welcome to SanrioTCG! :3",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = Color(0xFF7687D3)
                )
            )
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = "Your Current Challenges:",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = Color(0xFF7687D3)
            )
            Spacer(modifier = Modifier.height(16.dp))

            challenges.forEach { challenge ->
                ChallengeItem(challenge, challengeRepository)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    rewardCoins = calculateRewardCoins()
                    showClaimDialog = true
                },
                enabled = hasUnclaimedRewards,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
            ) {
                Text("Claim Rewards", color = Color.White, fontFamily = FontFamily.SansSerif)
            }

            if (showClaimDialog) {
                AlertDialog(
                    onDismissRequest = { showClaimDialog = false },
                    title = { Text("Claim Rewards", fontFamily = FontFamily.SansSerif) },
                    text = { Text("You have earned $rewardCoins coins.", fontFamily = FontFamily.SansSerif) },
                    confirmButton = {
                        Button(
                            onClick = {
                                onCoinBalanceChange(coinBalance + rewardCoins)

                                challenges.filter { it.progress >= it.target && !it.claimed }
                                    .forEach { challenge ->
                                        coroutineScope.launch {
                                            challengeRepository.markAsClaimed(challenge.id)
                                        }
                                        challenge.claimed = true
                                    }

                                rewardCoins = 0
                                showClaimDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
                        ) {
                            Text("Claim", color = Color.White, fontFamily = FontFamily.SansSerif)
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showClaimDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
                        ) {
                            Text("Cancel", color = Color.White, fontFamily = FontFamily.SansSerif)
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun ChallengeItem(challenge: Challenge, challengeRepository: ChallengeRepository) {
    val progress = remember { mutableIntStateOf(challenge.progress) }

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Text(
            text = challenge.name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = Color(0xFF7687D3)
        )
        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress.intValue / challenge.target.toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = Color(0xFF7687D3),
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${progress.intValue} / ${challenge.target} - Reward: ${challenge.reward} coins",
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp),
            color = Color.Gray
        )
    }

    LaunchedEffect(progress.intValue) {
        challengeRepository.updateProgress(challenge.id, progress.intValue)
    }
}


@Composable
fun CollectionPage(navController: NavHostController, collectedCards: List<Card>) {
    val maxCards = 19
    var showCongratulations by rememberSaveable { mutableStateOf(false) }
    var hasShownCongratulations by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(collectedCards.size) {
        if (collectedCards.size == maxCards && !hasShownCongratulations) {
            showCongratulations = true
            hasShownCongratulations = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF0FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Card Collection",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFF7687D3)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Collected Cards: ${collectedCards.size}/$maxCards",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                ),
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp)
            ) {
                items(collectedCards) { card ->
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate("fullScreenCard/${card.id}")
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDBF7).copy(alpha = 0.5f))
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = card.drawableRes),
                                contentDescription = "Card Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }
        }

        if (showCongratulations) {
            CongratulationsPopup {
                showCongratulations = false
            }
        }
    }
}


@Composable
fun FullScreenCardPage(
    cardId: Int,
    navController: NavHostController,
    cardRepository: CardRepository,
    onSell: (Int) -> Unit
) {
    val cardPrices = mapOf(
        1 to 50,  // Cinnamoroll
        2 to 50,  // Pompompudding
        3 to 50,  // Maria
        4 to 50,  // Pochacco
        5 to 70,  // Ichigo Man to the rescue!
        6 to 70,  // Cinnamon Pile
        7 to 70,  // Picnic With Friends
        8 to 70,  // Snack Time
        9 to 100,  // Tuxedo Sam
        10 to 100,  // Kero Kero Keroppi
        11 to 100,  // Pompompurin
        12 to 100,  // Hello Kitty
        13 to 100,  // My Melody
        14 to 200,  // Best Friends Forever
        15 to 200,  // Chococat
        16 to 200,  // Kiki
        17 to 200,  // Lala
        18 to 500,  // Shadow
        19 to 1000  // Michail
    )

    var card by remember { mutableStateOf<Card?>(null) }

    LaunchedEffect(cardId) {
        card = cardRepository.getCardById(cardId)
    }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0FB)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            card.let {
                if (it != null) {
                    Image(
                        painter = painterResource(id = it.drawableRes),
                        contentDescription = "Full Screen Card",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    val price = cardPrices[cardId] ?: 0
                    Text(
                        text = "Sell for $price Coins",
                        color = Color(0xFFFF6961),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable {
                                coroutineScope.launch {
                                    if (it != null) {
                                        cardRepository.delete(it)
                                    }
                                    onSell(price)
                                    navController.popBackStack()
                                }
                            }
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Back",
                        color = Color(0xFF7687D3),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun CongratulationsPopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Congratulations!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color(0xFF7687D3)
                    )
                )
            }
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You have collected all cards!",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        color = Color.Gray
                    ),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
                ) {
                    Text(text = "OK", color = Color.White)
                }
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}


@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumPage(
    collectedCards: List<Card>,
    albumRepository: AlbumRepository
) {
    var albumName by remember { mutableStateOf("") }
    var selectedCards by remember { mutableStateOf(setOf<Card>()) }
    var isCreatingAlbum by remember { mutableStateOf(false) }
    var selectedAlbum by remember { mutableStateOf<Album?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var isEditingAlbum by remember { mutableStateOf(false) }
    val albums by albumRepository.allAlbums.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF0FB))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (isCreatingAlbum) {
                LaunchedEffect(isCreatingAlbum) {
                    albumName = ""
                    selectedCards = emptySet()
                }

                TextField(
                    value = albumName,
                    onValueChange = { albumName = it },
                    label = {
                        Text(
                            "Album Name",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF7687D3),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(
                            Color(0xFFE8EAF6),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedLabelColor = Color(0xFF7687D3),
                        unfocusedLabelColor = Color(0xFF7687D3),
                        focusedIndicatorColor = Color(0xFF7687D3),
                        unfocusedIndicatorColor = Color(0xFFB0BEC5),
                        containerColor = Color.Transparent,
                        focusedTextColor = Color(0xFF7687D3),
                        unfocusedTextColor = Color(0xFF7687D3),
                        cursorColor = Color(0xFF7687D3)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Cards for Album", style = MaterialTheme.typography.bodyLarge)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    items(collectedCards) { card ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    selectedCards = if (selectedCards.contains(card)) {
                                        selectedCards - card
                                    } else {
                                        selectedCards + card
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selectedCards.contains(card)) Color(0xFF7687D3)
                                else Color(0xFFFFDBF7).copy(alpha = 0.5f)
                            )
                        ) {
                            Image(
                                painter = painterResource(id = card.drawableRes),
                                contentDescription = "Card Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val newAlbum = Album(
                            name = albumName,
                            cardIds = selectedCards.map { it.id }
                        )

                        coroutineScope.launch {
                            albumRepository.insert(newAlbum)
                        }

                        isCreatingAlbum = false
                        albumName = ""
                        selectedCards = emptySet()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = albumName.isNotEmpty() && selectedCards.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7687D3)
                    )
                ) {
                    Text("Create Album")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isCreatingAlbum = false
                        albumName = ""
                        selectedCards = emptySet()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7687D3)
                    )
                ) {
                    Text("Cancel")
                }
            } else if (selectedAlbum != null) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            isEditingAlbum = true
                            albumName = selectedAlbum?.name ?: ""
                            selectedCards = collectedCards.filter { card ->
                                selectedAlbum?.cardIds?.contains(card.id) == true
                            }.toSet()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Album",
                            tint = Color(0xFF7687D3)
                        )
                    }

                    Button(
                        onClick = { selectedAlbum = null },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7687D3)
                        )
                    ) {
                        Text("Back to Albums")
                    }

                    IconButton(
                        onClick = {
                            showDeleteConfirmation = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Album",
                            tint = Color(0xFFFF6961)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditingAlbum) {
                    TextField(
                        value = albumName,
                        onValueChange = { albumName = it },
                        label = {
                            Text(
                                "Album Name",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF7687D3),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(
                                Color(0xFFE8EAF6),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedLabelColor = Color(0xFF7687D3),
                            unfocusedLabelColor = Color(0xFF7687D3),
                            focusedIndicatorColor = Color(0xFF7687D3),
                            unfocusedIndicatorColor = Color(0xFFB0BEC5),
                            containerColor = Color.Transparent,
                            focusedTextColor = Color(0xFF7687D3),
                            unfocusedTextColor = Color(0xFF7687D3),
                            cursorColor = Color(0xFF7687D3)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Select Cards for Album", style = MaterialTheme.typography.bodyLarge)

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(collectedCards) { card ->
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .clickable {
                                        selectedCards = if (selectedCards.contains(card)) {
                                            selectedCards - card
                                        } else {
                                            selectedCards + card
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedCards.contains(card)) Color(
                                        0xFF7687D3
                                    ) else Color(0xFFFFDBF7).copy(alpha = 0.5f)
                                )
                            ) {
                                Image(
                                    painter = painterResource(id = card.drawableRes),
                                    contentDescription = "Card Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            selectedAlbum?.let { album ->
                                val updatedAlbum = album.copy(
                                    name = albumName,
                                    cardIds = selectedCards.map { card -> card.id })

                                coroutineScope.launch {
                                    albumRepository.update(updatedAlbum)

                                    selectedAlbum = updatedAlbum
                                }
                            }
                            isEditingAlbum = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7687D3)
                        )
                    ) {
                        Text("Save Changes")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isEditingAlbum = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7687D3)
                        )
                    ) {
                        Text("Cancel")
                    }
                } else {
                    Text(
                        text = selectedAlbum?.name ?: "",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = Color(0xFF7687D3)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val cardsInAlbum = collectedCards.filter { card ->
                        selectedAlbum?.cardIds?.contains(card.id) == true
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(cardsInAlbum) { card ->
                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDBF7).copy(alpha = 0.5f))
                            ) {
                                Image(
                                    painter = painterResource(id = card.drawableRes),
                                    contentDescription = "Card Image",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Your Albums",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = Color(0xFF7687D3)
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (albums.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        items(albums) { album ->
                            val albumCards = collectedCards.filter { card -> album.cardIds.contains(card.id) }
                            val thumbnailCard = albumCards.firstOrNull()

                            Card(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedAlbum = album
                                    }
                                    .border(2.dp, Color(0xFF7687D3), RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFDBF7).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = album.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${albumCards.size} Cards",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color(0xFF7687D3)
                                        )
                                    }

                                    if (thumbnailCard != null) {
                                        Image(
                                            painter = painterResource(id = thumbnailCard.drawableRes),
                                            contentDescription = "Thumbnail",
                                            modifier = Modifier
                                                .size(120.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text("No albums available.")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { isCreatingAlbum = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7687D3)
                    )
                ) {
                    Text("Start Creating Album")
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirm Deletion") },
            text = {
                Text("Are you sure you want to delete the album '${selectedAlbum?.name}'? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedAlbum?.let { album ->
                            coroutineScope.launch {
                                albumRepository.delete(album)
                            }
                        }
                        selectedAlbum = null
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7687D3)
                    )
                ) {
                    Text("Yes, Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF7687D3)
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


@Composable
fun ShopPage(
    navController: NavHostController,
    coinBalance: Int,
    onCoinBalanceChange: (Int) -> Unit,
    onCardsAdded: (List<Card>) -> Unit,
    challenges: List<Challenge>,
    totalSpent: MutableState<Int>,
    challengeRepository: ChallengeRepository
) {
    var showErrorMessage by remember { mutableStateOf(false) }

    val packPrice = 100

    val cardsWithRarity = listOf(
        // Common
        Card(id = 1, drawableRes = R.drawable.cinnamoroll, rarity = 0.08125f, name = "Cinnamoroll"),
        Card(id = 2, drawableRes = R.drawable.pompompudding, rarity = 0.08125f, name = "Pompompudding"),
        Card(id = 3, drawableRes = R.drawable.maria, rarity = 0.08125f, name = "Maria"),
        Card(id = 4, drawableRes = R.drawable.pochacco, rarity = 0.08125f, name = "Pochacco"),
        // Trap and Spell - Common
        Card(id = 5, drawableRes = R.drawable.ichigo_man, rarity = 0.08125f, name = "Ichigo Man to the rescue!"),
        Card(id = 6, drawableRes = R.drawable.cinnamon_pile, rarity = 0.08125f, name = "Cinnamon Pile"),
        Card(id = 7, drawableRes = R.drawable.picnic_with_friends, rarity = 0.08125f, name = "Picnic With Friends"),
        Card(id = 8, drawableRes = R.drawable.snack_time, rarity = 0.08125f, name = "Snack Time"),
        // Uncommon
        Card(id = 9, drawableRes = R.drawable.tuxedo_sam, rarity = 0.05f, name = "Tuxedo Sam"),
        Card(id = 10, drawableRes = R.drawable.keroppi, rarity = 0.05f, name = "Kero Kero Keroppi"),
        Card(id = 11, drawableRes = R.drawable.pompompurin, rarity = 0.05f, name = "Pompompurin"),
        Card(id = 12, drawableRes = R.drawable.hello_kitty, rarity = 0.05f, name = "Hello Kitty"),
        Card(id = 13, drawableRes = R.drawable.my_melody, rarity = 0.05f, name = "My Melody"),
        // Rare
        Card(id = 14, drawableRes = R.drawable.best_friends_forever, rarity = 0.02f, name = "Best Friends Forever"),
        Card(id = 15, drawableRes = R.drawable.chococat, rarity = 0.02f, name = "Chococat"),
        Card(id = 16, drawableRes = R.drawable.kiki, rarity = 0.02f, name = "Little Twin Stars Kiki"),
        Card(id = 17, drawableRes = R.drawable.lala, rarity = 0.02f, name = "Little Twin Stars Lala"),
        // Legendary
        Card(id = 18, drawableRes = R.drawable.shadow, rarity = 0.01f, name = "Shadow"),
        Card(id = 19, drawableRes = R.drawable.michail, rarity = 0.01f, name = "Michail")
    )

    fun getRandomCard(): Card {
        val randomValue = Math.random().toFloat()
        var cumulativeProbability = 0f
        for (card in cardsWithRarity) {
            cumulativeProbability += card.rarity
            if (randomValue <= cumulativeProbability) {
                return card
            }
        }
        return cardsWithRarity.first()
    }

    val coroutineScope = rememberCoroutineScope()

    fun buyPack() {
        if (coinBalance >= packPrice) {
            onCoinBalanceChange(coinBalance - packPrice)

            val newCards = List(3) { getRandomCard() }
            onCardsAdded(newCards)

            totalSpent.value += packPrice

            challenges.forEach { challenge ->
                if (challenge.name == "Open 10 Packs") {
                    challenge.progress = minOf(challenge.progress + 1, challenge.target)
                    coroutineScope.launch {
                        challengeRepository.updateProgress(challenge.id, challenge.progress)
                    }
                }

                newCards.forEach { card ->
                    if (card.rarity < 0.03) {
                        if (challenge.name == "Collect 5 Rare Cards") {
                            challenge.progress = minOf(challenge.progress + 1, challenge.target)
                            coroutineScope.launch {
                                challengeRepository.updateProgress(challenge.id, challenge.progress)
                            }
                        }
                    }
                }

                if (challenge.name == "Spend 500 Coins") {
                    challenge.progress = minOf(totalSpent.value, challenge.target)
                    coroutineScope.launch {
                        challengeRepository.updateProgress(challenge.id, challenge.progress)
                    }
                }
            }

            navController.navigate("packOpening/${newCards.joinToString(",") { it.id.toString() }}")
        } else {
            showErrorMessage = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF0FB))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Coins: $coinBalance",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFF7687D3)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.card_pack),
                    contentDescription = "Card Pack",
                    modifier = Modifier
                        .clickable { buyPack() }
                        .size(275.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Price: $packPrice Coins",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = Color(0xFF7687D3)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showErrorMessage) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Out of money? Play a game!",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.navigate("minigame") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
                    ) {
                        Text(text = "Play Minigame")
                    }
                }
            }
        }
    }
}


@Composable
fun PackOpeningScreen(
    cardIds: List<Int>,
    navController: NavHostController
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            delay(300)
            currentIndex++
            isAnimating = false
        }
    }

    val transition = updateTransition(targetState = isAnimating, label = "Card Transition")

    val scale by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 300, easing = FastOutSlowInEasing)
        },
        label = "Scale Animation"
    ) { animating -> if (animating) 0.5f else 1f }

    val alpha by transition.animateFloat(
        transitionSpec = {
            tween(durationMillis = 300, easing = LinearEasing)
        },
        label = "Alpha Animation"
    ) { animating -> if (animating) 0f else 1f }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(500.dp)
                    .aspectRatio(1f)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        alpha = alpha
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getDrawableResByCardId(cardIds[currentIndex])),
                    contentDescription = "Revealed Card",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (currentIndex < cardIds.size - 1) "Next" else "Finish",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF7687D3),
                modifier = Modifier
                    .clickable {
                        if (currentIndex < cardIds.size - 1) {
                            if (!isAnimating) {
                                isAnimating = true
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                    .padding(8.dp)
            )
        }
    }
}


@Composable
fun MinigamePage(
    navController: NavHostController,
    onCoinBalanceChange: (Int) -> Unit
) {
    var earnedCoins by remember { mutableIntStateOf(0) }
    var circles by remember { mutableStateOf(listOf<Circle>()) }
    var showDialog by remember { mutableStateOf(false) }
    var isSpawningCircles by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val paddingTop = 0.dp
    val paddingBottom = 300.dp

    fun generateRandomCircle(): Circle {
        val id = Random.nextInt()
        val x = Random.nextFloat() * 0.8f + 0.1f
        val y = Random.nextFloat() * 0.8f + 0.1f
        return Circle(id, x, y)
    }

    fun removeCircleAfterDelay(circle: Circle, delayMillis: Long) {
        coroutineScope.launch {
            delay(delayMillis)
            circles = circles.filter { it.id != circle.id }
        }
    }

    LaunchedEffect(isSpawningCircles) {
        while (isSpawningCircles) {
            val newCircle = generateRandomCircle()
            circles = circles + newCircle
            removeCircleAfterDelay(newCircle, 1000L)
            delay(1000L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF0FB))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingTop, bottom = paddingBottom)
        ) {
            circles.forEach { circle ->
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .offset(
                            x = (circle.x * screenWidth.value).dp,
                            y = (circle.y * (screenHeight.value - paddingTop.value - paddingBottom.value)).dp + paddingTop
                        )
                        .clip(CircleShape)
                        .clickable {
                            earnedCoins += 10
                            circles = circles.filter { it.id != circle.id }
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.coin),
                        contentDescription = "Coin Circle",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Coins Earned: $earnedCoins",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    color = Color(0xFF7687D3),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )
        }

        Button(
            onClick = {
                isSpawningCircles = false
                showDialog = true
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
        ) {
            Text(
                text = "Return to Shop",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFFF0FB),
                    fontSize = 16.sp
                )
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = "Good Job!",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF7687D3)
                        )
                    )
                },
                text = {
                    Text(
                        text = "You earned $earnedCoins coins!",
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp,
                            color = Color(0xFF7687D3)
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onCoinBalanceChange(earnedCoins)
                            earnedCoins = 0
                            showDialog = false
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7687D3))
                    ) {
                        Text(
                            text = "OK",
                            style = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFFFFF0FB)
                            )
                        )
                    }
                }
            )
        }
    }
}


fun getDrawableResByCardId(cardId: Int): Int {
    return when (cardId) {
        1 -> R.drawable.cinnamoroll
        2 -> R.drawable.pompompudding
        3 -> R.drawable.maria
        4 -> R.drawable.pochacco
        5 -> R.drawable.ichigo_man
        6 -> R.drawable.cinnamon_pile
        7 -> R.drawable.picnic_with_friends
        8 -> R.drawable.snack_time
        9 -> R.drawable.tuxedo_sam
        10 -> R.drawable.keroppi
        11 -> R.drawable.pompompurin
        12 -> R.drawable.hello_kitty
        13 -> R.drawable.my_melody
        14 -> R.drawable.best_friends_forever
        15 -> R.drawable.chococat
        16 -> R.drawable.kiki
        17 -> R.drawable.lala
        18 -> R.drawable.shadow
        19 -> R.drawable.michail
        else -> R.drawable.logo
    }
}

@Composable
fun InfoPage() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFFFF0FB))
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Info / Help",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color(0xFF7687D3)
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            InfoSection(
                title = "How Challenges Work",
                content = """
                    Challenges are tasks you can complete to earn coins and rewards. 
                    Some challenges may require you to open card packs, collect rare cards, or sell cards. 
                    Completing challenges will reward you with coins, which can be used to buy more card packs!
                """.trimIndent()
            )

            InfoSection(
                title = "Collecting Cards",
                content = """
                    You can collect cards by purchasing and opening card packs in the Card Shop. 
                    Each pack contains 3 random cards with varying rarities. 
                    
                    Cards are categorized into common, uncommon, rare and legendary types.
                    Rarity is determined by the amount of stars.
                    Spell and Trap cards don't have a star value, but are treated like Common cards.
                    
                    Common cards have either 2 or 3 stars.
                    Uncommon cards have 4 or 5 stars.
                    Rare cards have 6 or 7 stars.
                    And Legendary cards have 8 stars.
                """.trimIndent()
            )

            InfoSection(
                title = "How to Earn Coins",
                content = """
                    You can earn coins in several ways:
                    - Completing challenges.
                    - Selling unwanted cards.
                    - Playing Minigame.
                    
                    Use your coins to buy more card packs and continue expanding your collection!
                """.trimIndent()
            )

            InfoSection(
                title = "How the Minigame Works",
                content = """
                    The Minigame, which can only be played when you're out of coins, allows you to earn coins by clicking on randomly spawning coin circles.
                    - Coins spawn at random positions on the screen.
                    - Each time you click on a coin, you earn 10 coins.
                    - The coins will disappear after 1 second, so be quick to collect them!
                    - You can stop the minigame at any time by clicking the 'Return to Shop' button.
                """.trimIndent()
            )

            InfoSection(
                title = "Creating Albums",
                content = """
                    In the Albums Section, you can create albums to organize and showcase your favorite cards.
                    Collect and save your most prized cards to display them in your albums!
                """.trimIndent()
            )

            InfoSection(
                title = "Card Drop Rates",
                content = """
                    The odds of getting different types of cards are as follows:
                    - Common Cards: 65% chance
                    - Uncommon Cards: 25% chance
                    - Rare Cards: 8% chance
                    - Legendary Cards: 2% chance
                    
                    These odds apply each time you open a card pack. Good luck on your collection journey!
                """.trimIndent()
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "©\nThomas Stopper, cc231012\nLeonie Kozak, cc231010\n\nSanrioTCG, 2025",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        color = Color(0xFF7687D3)
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}


@Composable
fun InfoSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF7687D3)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                color = Color(0xFF555555)
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}