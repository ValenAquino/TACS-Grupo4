export const resolverTipo = (metodos = []) => {
  if (metodos.includes('INTERCAMBIO') && metodos.includes('SUBASTA')) return 'ambos'
  return metodos.includes('SUBASTA') ? 'subasta' : 'intercambio'
}
