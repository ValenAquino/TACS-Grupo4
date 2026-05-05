const BarraItem = ({ label, valor, total, color }) => {
  const pct = total > 0 ? Math.min(100, Math.round((valor / total) * 100)) : 0
  return (
    <div className="d-flex flex-column gap-1">
      <div className="d-flex justify-content-between align-items-center">
        <span style={{ fontSize: '0.88rem', color: 'var(--color-text)' }}>{label}</span>
        <span style={{ fontSize: '0.88rem', fontWeight: 600, color: 'var(--color-text)' }}>
          {valor}
        </span>
      </div>
      <div style={{ background: '#e9ecef', borderRadius: 4, height: 8 }}>
        <div
          style={{
            width: `${pct}%`,
            background: color,
            borderRadius: 4,
            height: '100%',
            transition: 'width 0.4s ease',
          }}
        />
      </div>
    </div>
  )
}

export default BarraItem
