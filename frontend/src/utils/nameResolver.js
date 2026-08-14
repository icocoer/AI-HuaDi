/**
 * 将ID列表转换为带重名标记的名字映射
 * @param {Array} list - 包含id和name的数组
 * @returns {Map} id -> 显示名称 的映射
 */
export function buildNameMap(list) {
  const nameMap = new Map()
  const nameCount = {}

  // 统计每个名字出现的次数
  list.forEach(item => {
    const name = item.name || item.realName || item.username
    if (name) {
      nameCount[name] = (nameCount[name] || 0) + 1
    }
  })

  // 记录每个名字已使用的序号
  const nameIndex = {}

  list.forEach(item => {
    const name = item.name || item.realName || item.username
    if (name) {
      if (nameCount[name] > 1) {
        nameIndex[name] = (nameIndex[name] || 0) + 1
        nameMap.set(item.id, `${name}${nameIndex[name]}`)
      } else {
        nameMap.set(item.id, name)
      }
    }
  })

  return nameMap
}

/**
 * 获取显示名称
 * @param {Map} nameMap - 名字映射
 * @param {number|string} id - ID
 * @returns {string} 显示名称
 */
export function getDisplayName(nameMap, id) {
  return nameMap.get(id) || String(id)
}
