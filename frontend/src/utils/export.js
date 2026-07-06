import * as XLSX from 'xlsx'

export function exportToExcel(data, columns, filename = 'export') {
  const headers = columns.map(c => c.label)
  const rows = data.map(row =>
    columns.map(c => {
      const val = row[c.prop]
      return val != null ? val : ''
    })
  )

  const wsData = [headers, ...rows]
  const ws = XLSX.utils.aoa_to_sheet(wsData)

  const colWidths = columns.map((c, i) => {
    const maxLen = Math.max(
      c.label.length,
      ...rows.map(r => String(r[i] || '').length)
    )
    return { wch: Math.min(maxLen + 2, 40) }
  })
  ws['!cols'] = colWidths

  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, 'Sheet1')
  XLSX.writeFile(wb, `${filename}.xlsx`)
}
