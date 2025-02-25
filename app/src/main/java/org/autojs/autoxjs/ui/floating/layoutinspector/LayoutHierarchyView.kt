package org.autojs.autoxjs.ui.floating.layoutinspector

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import com.stardust.autojs.core.ozobi.capture.ScreenCapture.Companion.curImgBitmap
import com.stardust.autojs.core.ozobi.capture.ScreenCapture.Companion.isCurImgBitmapValid
import com.stardust.util.Ozobi
import com.stardust.util.ViewUtil
import com.stardust.view.accessibility.NodeInfo
import kotlinx.coroutines.DelicateCoroutinesApi
import org.autojs.autoxjs.R
import org.autojs.autoxjs.ui.floating.layoutinspector.LayoutHierarchyFloatyWindow.Companion.mSelectedNode
import org.autojs.autoxjs.ui.widget.LevelBeamView
import pl.openrnd.multilevellistview.ItemInfo
import pl.openrnd.multilevellistview.MultiLevelListAdapter
import pl.openrnd.multilevellistview.MultiLevelListView
import pl.openrnd.multilevellistview.NestType
import pl.openrnd.multilevellistview.OnItemClickListener
import java.util.Locale
import java.util.Stack

/**
 * Created by Stardust on 2017/3/10.
 */
open class LayoutHierarchyView : MultiLevelListView {
    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, nodeInfo: NodeInfo)
    }

    var mAdapter: Adapter? = null
    private var mOnItemLongClickListener: ((view: View, nodeInfo: NodeInfo) -> Unit)? = null
    private var onItemTouchListener: ((view: View, event: MotionEvent) -> Boolean)? = null
    private val mOnItemLongClickListenerProxy =
        AdapterView.OnItemLongClickListener { parent, view, position, id ->
            (view.tag as ViewHolder).nodeInfo?.let {
                mOnItemLongClickListener?.invoke(view, it)
                return@OnItemLongClickListener true
            }
            false
        }
    
    companion object{
        var nightMode = false
    }
    // <

    var boundsPaint: Paint? = null
        private set
    private var mBoundsInScreen: IntArray? = null
    var mStatusBarHeight = 0
    var mClickedNodeInfo: NodeInfo? = null
    private var mClickedView: View? = null
    private var mOriginalBackground: Drawable? = null
    var mShowClickedNodeBounds = false
    private var mClickedColor = -0x664d4c49
    private var mRootNode: NodeInfo? = null
    private val mInitiallyExpandedNodes: MutableSet<NodeInfo?> = HashSet()
    private var isAuth = false

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    fun setShowClickedNodeBounds(showClickedNodeBounds: Boolean) {
        mShowClickedNodeBounds = showClickedNodeBounds
    }

    fun setClickedColor(clickedColor: Int) {
        mClickedColor = clickedColor
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        
        isAuth = Ozobi.authenticate(context)
        if(nightMode){
            LevelBeamView.levelInfoTextColor = Color.WHITE
        }else{
            LevelBeamView.levelInfoTextColor = Color.BLACK
        }
        LevelBeamView.selectedNode = mSelectedNode
        // <
        mAdapter = Adapter()
        setAdapter(mAdapter)
        nestType = NestType.MULTIPLE
        (getChildAt(0) as ListView).apply {
            setOnTouchListener { view, motionEvent ->
                return@setOnTouchListener onItemTouchListener?.invoke(view, motionEvent) ?: false
            }
            onItemLongClickListener = mOnItemLongClickListenerProxy
        }
        setWillNotDraw(false)
        initPaint()
        setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(
                parent: MultiLevelListView,
                view: View,
                item: Any,
                itemInfo: ItemInfo
            ) {
                setClickedItem(view, item as NodeInfo)
            }

            override fun onGroupItemClicked(
                parent: MultiLevelListView,
                view: View,
                item: Any,
                itemInfo: ItemInfo
            ) {
                setClickedItem(view, item as NodeInfo)
            }
        })
    }

    private fun setClickedItem(view: View, item: NodeInfo) {
        mClickedNodeInfo = item
        if (mClickedView == null) {
            mOriginalBackground = view.background
        } else {
            mClickedView!!.background = mOriginalBackground
        }
        mClickedView = view
        invalidate()
    }

    private fun initPaint() {
        boundsPaint = Paint()
        boundsPaint!!.color = Color.DKGRAY
        boundsPaint!!.style = Paint.Style.STROKE
        boundsPaint!!.isAntiAlias = true
        boundsPaint!!.strokeWidth = 10f
        mStatusBarHeight = ViewUtil.getStatusBarHeight(context)
    }

    fun setRootNode(rootNodeInfo: NodeInfo) {
        mRootNode = rootNodeInfo
        mAdapter!!.setDataItems(listOf(rootNodeInfo))
        mClickedNodeInfo = null
        mInitiallyExpandedNodes.clear()
    }

    fun setOnItemTouchListener(listener: ((view: View, event: MotionEvent) -> Boolean)) {
        onItemTouchListener = listener
    }

    fun setOnItemLongClickListener(onNodeInfoSelectListener: (view: View, nodeInfo: NodeInfo) -> Unit) {
        mOnItemLongClickListener = onNodeInfoSelectListener
    }
    
    fun expandChild(nodeInfo: NodeInfo?){
        if(nodeInfo == null){
            return
        }
        val children = nodeInfo.getChildren()
        for(child in children){
            mInitiallyExpandedNodes.add(child)
            expandChild(child)
        }
    }
    fun expand(){
//        mInitiallyExpandedNodes.clear()
        expandChild(mClickedNodeInfo)
        val parents = Stack<NodeInfo?>()
        mClickedNodeInfo?.let { searchNodeParents(it, mRootNode, parents) }
        mInitiallyExpandedNodes.addAll(parents)
        mAdapter?.reloadData()
    }
    // <
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if(isAuth){
            if (isCurImgBitmapValid && curImgBitmap != null) {
                if (width == curImgBitmap!!.height || height == curImgBitmap!!.width) {
                    
                } else {
                    canvas.drawBitmap(curImgBitmap!!, 0f, -mStatusBarHeight.toFloat(), null)
                }
            }
        }
        if (mBoundsInScreen == null) {
            mBoundsInScreen = IntArray(4)
            getLocationOnScreen(mBoundsInScreen)
            mStatusBarHeight = mBoundsInScreen!![1]
        }
        if (mShowClickedNodeBounds && mClickedNodeInfo != null) {
            LayoutBoundsView.drawRect(
                canvas,
                mClickedNodeInfo!!.boundsInScreen,
                mStatusBarHeight,
                boundsPaint
            )
        }
    }

    fun setSelectedNode(selectedNode: NodeInfo) {
        mInitiallyExpandedNodes.clear()
        val parents = Stack<NodeInfo?>()
        LayoutHierarchyFloatyWindow.curSelectedNodeParents.clear()
        searchNodeParents(selectedNode, mRootNode, parents)
        if(parents.isNotEmpty()){
            mClickedNodeInfo = parents.peek()
            LayoutHierarchyFloatyWindow.curSelectedNodeChildren = mClickedNodeInfo!!.getChildren()
            LayoutHierarchyFloatyWindow.curSelectedNodeParents.clear()
            getParentsList(mClickedNodeInfo, LayoutHierarchyFloatyWindow.curSelectedNodeParents)
            LayoutHierarchyFloatyWindow.curSelectedNodeParents.removeAt(0)
            LayoutHierarchyFloatyWindow.curSelectedBrotherList = getBrotherList(mClickedNodeInfo)
        }
        mInitiallyExpandedNodes.addAll(parents)
        mAdapter!!.reloadData()
    }

    
    fun ozobiSetSelectedNode(selectedNode: NodeInfo){
        mClickedNodeInfo = selectedNode
        LayoutHierarchyFloatyWindow.curSelectedNodeChildren = mClickedNodeInfo!!.getChildren()
        LayoutHierarchyFloatyWindow.curSelectedNodeParents.clear()
        getParentsList(mClickedNodeInfo, LayoutHierarchyFloatyWindow.curSelectedNodeParents)
        LayoutHierarchyFloatyWindow.curSelectedNodeParents.removeAt(0)
        LayoutHierarchyFloatyWindow.curSelectedBrotherList = getBrotherList(mClickedNodeInfo)
        LevelBeamView.selectedNode = mClickedNodeInfo
        if(mInitiallyExpandedNodes.contains(mClickedNodeInfo)){
            if(LayoutHierarchyFloatyWindow.canCollapse){
                mInitiallyExpandedNodes.remove(mClickedNodeInfo)
            }
        }else{
            mInitiallyExpandedNodes.add(mClickedNodeInfo)
        }
        mAdapter!!.reloadData()
    }
    fun getParentsList(nodeInfo:NodeInfo?, nodeList:MutableList<NodeInfo?>){
        if(nodeInfo == null){
            return
        }
        nodeList.add(nodeInfo)
        getParentsList(nodeInfo.parent, nodeList)
    }
    fun getBrotherList(nodeInfo:NodeInfo?):List<NodeInfo>?{
        if(nodeInfo?.parent != null){
            return nodeInfo.parent!!.getChildren()
        }
        return null
    }
    // <

    private fun searchNodeParents(
        nodeInfo: NodeInfo,
        rootNode: NodeInfo?,
        stack: Stack<NodeInfo?>
    ): Boolean {
        stack.push(rootNode)
        if (nodeInfo == rootNode) {
            return true
        }
        var found = false
        for (child in rootNode!!.getChildren()) {
            if (searchNodeParents(nodeInfo, child, stack)) {
                found = true
                break
            }
        }
        if (!found) {
            stack.pop()
        }
        return found
    }

    private inner class ViewHolder internal constructor(view: View) {
        var nameView: TextView
        var infoView: TextView
        var arrowView: ImageView
        var levelBeamView: LevelBeamView
        var nodeInfo: NodeInfo? = null

        init {
            infoView = view.findViewById<View>(R.id.dataItemInfo) as TextView
            nameView = view.findViewById<View>(R.id.dataItemName) as TextView
            arrowView = view.findViewById<View>(R.id.dataItemArrow) as ImageView
            levelBeamView = view.findViewById<View>(R.id.dataItemLevelBeam) as LevelBeamView
        }
    }

    inner class Adapter : MultiLevelListAdapter() {
        override fun getSubObjects(`object`: Any): List<*> {
            return (`object` as NodeInfo).getChildren()
        }

        override fun isExpandable(`object`: Any): Boolean {
            return (`object` as NodeInfo).getChildren().isNotEmpty()
        }

        override fun isInitiallyExpanded(`object`: Any): Boolean {
            return mInitiallyExpandedNodes.contains(`object` as NodeInfo)
        }

        public override fun getViewForObject(
            `object`: Any,
            convertView: View?,
            itemInfo: ItemInfo
        ): View {
            var itemResource = R.layout.layout_hierarchy_view_item
            val nodeInfo = `object` as NodeInfo
            if(nightMode){
                itemResource = R.layout.layout_hierarchy_view_item_night
                LevelBeamView.levelInfoTextColor = Color.WHITE
            }else{
                LevelBeamView.levelInfoTextColor = Color.BLACK
            }
            val viewHolder: ViewHolder
            val convertView1 = if (convertView != null) {
                viewHolder = convertView.tag as ViewHolder

                convertView
            } else {
                val convertView2 =
                    LayoutInflater.from(context).inflate(itemResource, null)
                viewHolder = ViewHolder(convertView2)
                convertView2.tag = viewHolder

                convertView2
            }
            if(isAuth){
                var textInfo = ""
                var descInfo = ""
                nodeInfo.desc?.let{
                    if(it.isNotEmpty()){
                        descInfo += if(it.indexOf("\n") != -1){
                            it.substring(0,it.indexOf("\n"))
                        }else{
                            it
                        }
                    }
                }
                nodeInfo.text.let {
                    if(it.isNotEmpty()){
                        textInfo += if(it.indexOf("\n") != -1){
                            it.substring(0,it.indexOf("\n"))
                        }else{
                            it
                        }
                    }
                }
                val shotClassName = simplifyClassName(nodeInfo.className)
                viewHolder.nameView.text = shotClassName + "\n" +descInfo + "\n" + textInfo
            }else{
                viewHolder.nameView.text = simplifyClassName(nodeInfo.className)
            }
            viewHolder.nodeInfo = nodeInfo
//            if (viewHolder.infoView.visibility == VISIBLE) viewHolder.infoView.text =
//                getItemInfoDsc(itemInfo)
            if (itemInfo.isExpandable) {
                viewHolder.arrowView.visibility = VISIBLE
                viewHolder.arrowView.setImageResource(if (itemInfo.isExpanded) R.drawable.arrow_down else R.drawable.arrow_right)
            }else{
                viewHolder.arrowView.visibility = INVISIBLE
            }
            if (nodeInfo == mClickedNodeInfo) {
                convertView1?.let { setClickedItem(it, nodeInfo) }
            }
            if(isAuth){
                var isSelected = false
                var isParent = false
                var isChild = false
                var isBrother = false
                if(mSelectedNode == nodeInfo){
                    isSelected = true
                }
                LayoutHierarchyFloatyWindow.curSelectedNodeChildren?.let {
                    if(it.contains(nodeInfo)){
                        isChild = true
                    }
                }
                if(mClickedNodeInfo != nodeInfo && LayoutHierarchyFloatyWindow.curSelectedNodeParents.contains(nodeInfo)){
                    isParent = true
                }
                LayoutHierarchyFloatyWindow.curSelectedBrotherList?.let {
                    if(it.contains(nodeInfo)){
                        isBrother = true
                    }
                }
                viewHolder.levelBeamView.setAttr(itemInfo.level,nodeInfo.clickable,isSelected,isParent,isChild,isBrother)
            }
            return convertView1!!
        }

        private fun simplifyClassName(className: CharSequence?): String? {
            if (className == null) return null
            var s = className.toString()
            if (s.startsWith("android.widget.")) {
                s = s.substring(15)
            }
            return s
        }

        private fun getItemInfoDsc(itemInfo: ItemInfo): String {
            val builder = StringBuilder()
            builder.append(
                String.format(
                    Locale.getDefault(), "level[%d], idx in level[%d/%d]",
                    itemInfo.level + 1,  /*Indexing starts from 0*/
                    itemInfo.idxInLevel + 1 /*Indexing starts from 0*/,
                    itemInfo.levelSize
                )
            )
            if (itemInfo.isExpandable) {
                builder.append(String.format(", expanded[%b]", itemInfo.isExpanded))
            }
            return builder.toString()
        }
    }
}