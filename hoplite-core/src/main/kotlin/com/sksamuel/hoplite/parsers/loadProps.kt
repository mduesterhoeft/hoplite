package com.sksamuel.hoplite.parsers

import com.sksamuel.hoplite.MapNode
import com.sksamuel.hoplite.Node
import com.sksamuel.hoplite.Pos
import java.util.*

@Suppress("UNCHECKED_CAST")
fun loadProps(props: Properties, source: String): Node {

  val root = mutableMapOf<String, Any>()
  props.stringPropertyNames().toList().map { key ->

    val components = key.split('.')
    val map = components.fold(root) { acc, k ->
      acc.getOrPut(k) { mutableMapOf<String, Any>() } as MutableMap<String, Any>
    }
    map.put("____value", props.getProperty(key))
  }

  val pos = Pos.FilePos(source)

  fun Map<String, Any>.toNode(path: String): MapNode {
    val maps = filterValues { it is MutableMap<*, *> }.mapValues {
      when (val v = it.value) {
        is MutableMap<*, *> -> (v as MutableMap<String, Any>).toNode("$path.${it.key}")
        else -> throw java.lang.RuntimeException("Bug: unsupported state $it")
      }
    }
    val value = this["____value"]
    return MapNode(maps.toMap(), pos, "<root>", value)
  }

  return root.toNode("<root>")
}