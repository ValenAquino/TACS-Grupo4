import BarraItem from './barra-item'
import styles from '../administrador.module.css'

const SeccionBarras = ({ titulo, config, data }) => {
  const items = config.map((c) => ({ ...c, valor: data[c.key] ?? 0 }))
  const total = items.reduce((s, i) => s + i.valor, 0)
  return (
    <div className={styles.seccionCard}>
      <p className={styles.seccionTitulo}>{titulo}</p>
      <div className="d-flex flex-column gap-3">
        {items.map((item) => (
          <BarraItem
            key={item.label}
            label={item.label}
            valor={item.valor}
            total={total}
            color={item.color}
          />
        ))}
      </div>
    </div>
  )
}

export default SeccionBarras
