package br.com.qtota.ui.screen.store_detail

data class StoreDetail(
    val id: Long,
    val name: String,
    val urlLogo: String?,
    val products: List<StoreDetailProduct>,
    val branchList: List<StoreDetailBranch>
)
