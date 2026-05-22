const HeaderSeleccion = ({ modo, seleccionadas, bloqueadas }) => {
  const total = modo === 'unica' ? seleccionadas.length : seleccionadas.length + bloqueadas.length

  if (total === 0)
    return <span className="scroll-repetidas-header-seleccion">Ninguna seleccionada</span>

  const label =
    modo === 'unica' ? seleccionadas[0]?.jugador : `${total} seleccionada${total > 1 ? 's' : ''}`

  return <span className="scroll-repetidas-header-seleccion activa">{label}</span>
}

export default HeaderSeleccion
