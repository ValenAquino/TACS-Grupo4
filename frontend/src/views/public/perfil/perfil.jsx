import { useEffect, useState } from 'react'
import { buscarCalificaciones, buscarContadores, buscarPerfil, editarContrasenia, editarPerfil, } from '@/services/perfilService.js'
import { useAuth } from '@/contexts/userContext.jsx'
import Button from '@/components/ui/button/button.jsx'
import ConfirmModal from '@/components/ui/confirm-modal/confirm-modal.jsx'
import { useToast } from '@/contexts/toastContext.jsx'
import Paginacion from '@/components/ui/paginacion/paginacion.jsx'
import { useError } from '@/contexts/errorContext.jsx'
import { truncarADosDecimales } from '@/utils/estandarizar.jsx'

const renderStars = (score) => {
  const fullStars = Math.floor(score)
  const emptyStars = 5 - fullStars

  return (
    <>
      {'★'.repeat(fullStars)}
      {'☆'.repeat(emptyStars)}
    </>
  )
}

const Perfil = () => {
  const [showModal, setShowModal] = useState(false)
  const [nombreEditando, setNombreEditando] = useState('')
  const [nombreUsuarioEditando, setNombreUsuarioEditando] = useState('')
  const [contraseniaActual, setContraseniaActual] = useState('')
  const [contraseniaNueva, setContraseniaNueva] = useState('')
  const [mediosEditando, setMediosEditando] = useState([])
  const [indiceMedioEditando, setIndiceMedioEditando] = useState(-1)
  const [medioEditandoData, setMedioEditandoData] = useState({
    medio_comunicacion: 'TELEGRAM',
    valor: '',
  })
  const [nuevoMedioTipo, setNuevoMedioTipo] = useState('TELEGRAM')
  const [nuevoMedioValor, setNuevoMedioValor] = useState('')
  const [showContraseniaActual, setShowContraseniaActual] = useState(false)
  const [showContraseniaNueva, setShowContraseniaNueva] = useState(false)
  const [perfil, setPerfil] = useState({})
  const [reviews, setReviews] = useState([])
  const [loading, setLoading] = useState(true)
  const [loadingNotificaciones, setLoadingNotificaciones] = useState(false)
  const [stats, setStats] = useState([])
  const [pagina, setPagina] = useState(1)
  const [filtros, setFiltros] = useState({})

  const { handleError } = useError()
  const { showToast } = useToast()

  const [showConfirmModal, setShowConfirmModal] = useState(false)

  const { user, cerrarSesion } = useAuth()

  const perfilId = user.perfil_id

  const manejarCierreDeSesion = () => {
    try {
      cerrarSesion()
      showToast('Cierre de sesion exitoso', 'success')
    } catch (error) {
      showToast('Error al cerrar la sesion', 'error')
    }
  }

  useEffect(() => {
    const cargar = async () => {
      try {
        setLoading(true)

        const perfil = await buscarPerfil()
        const statsData = await buscarContadores()

        setPerfil(perfil)
        setStats(statsData)
      } catch (error) {
        handleError(error, () => {})
      } finally {
        setLoading(false)
      }
    }

    cargar()
  }, [])

  useEffect(() => {
    const cargarCalificaciones = async () => {
      try {
        setLoadingNotificaciones(true)

        const calificacionesApi = await buscarCalificaciones({
          ...filtros,
          pagina,
          limite: 10,
        })

        setReviews(calificacionesApi)
      } catch (error) {
        handleError(error, () => {})
      } finally {
        setLoadingNotificaciones(false)
      }
    }

    cargarCalificaciones()
  }, [filtros, pagina])

  const promedio = truncarADosDecimales(perfil.calificacion_media)

  const abrirModalEdicion = () => {
    setNombreEditando(perfil.nombre ?? '')
    setNombreUsuarioEditando(perfil.nombre_usuario ?? '')
    setContraseniaActual('')
    setContraseniaNueva('')
    setMediosEditando([...(perfil.medios_de_contacto ?? [])])
    setNuevoMedioTipo('TELEGRAM')
    setNuevoMedioValor('')
    setIndiceMedioEditando(-1)
    setShowModal(true)
  }

  const agregarMedio = () => {
    if (!nuevoMedioValor.trim()) return
    setMediosEditando((prev) => [
      ...prev,
      { medio_comunicacion: nuevoMedioTipo, valor: nuevoMedioValor },
    ])
    setNuevoMedioValor('')
  }

  const eliminarMedio = (i) => {
    setMediosEditando((prev) => prev.filter((_, idx) => idx !== i))
    if (indiceMedioEditando === i) setIndiceMedioEditando(-1)
  }

  const confirmarEdicionMedio = (i) => {
    setMediosEditando((prev) => prev.map((m, idx) => (idx === i ? { ...medioEditandoData } : m)))
    setIndiceMedioEditando(-1)
  }

  const guardarCambios = async () => {
    try {
      await editarPerfil({
        nombre: nombreEditando,
        nombreUsuario: nombreUsuarioEditando,
        mediosDeContacto: mediosEditando,
      })
      if (contraseniaNueva) {
        await editarContrasenia({ contraseniaActual, contraseniaNueva })
      }
      setPerfil((prev) => ({
        ...prev,
        nombre: nombreEditando,
        nombre_usuario: nombreUsuarioEditando,
        medios_de_contacto: mediosEditando,
      }))
      setShowModal(false)
      showToast('Perfil actualizado correctamente', 'success')
    } catch (error) {
      showToast('Error al guardar los cambios', 'error')
    }
  }

  return (
    <div className="d-flex flex-column">
      {/* HEADER */}
      <div
        style={{
          width: '100vw',
          position: 'relative',
          left: '50%',
          marginLeft: '-50vw',
          backgroundColor: '#175A2D',
          marginTop: '-32px',
        }}
      >
        <div
          className="mx-auto d-flex justify-content-between align-items-center px-4 py-4"
          style={{ maxWidth: '1100px' }}
        >
          <div className="d-flex align-items-center gap-3 text-white">
            <div
              style={{
                width: '90px',
                height: '90px',
                borderRadius: '50%',
                backgroundColor: '#3a7d5d',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '26px',
                border: '4px solid #629176',
              }}
            >
              MG
            </div>

            <div>
              {loading ? (
                <h2>Cargando datos...</h2>
              ) : (
                <>
                  <h2
                    className="mb-0 fw-bold"
                    style={{
                      maxWidth: '750px',
                      whiteSpace: 'nowrap',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                    }}
                  >
                    {perfil?.nombre}
                  </h2>
                  <div>
                    {renderStars(Number(promedio))} ⭐ {promedio} ({reviews.cantidad_de_elementos})
                  </div>
                </>
              )}
            </div>
          </div>

          <div className="d-flex align-items-center gap-3 ">
            <button
              className="btn btn-warning"
              style={{ padding: '10px 18px' }}
              onClick={abrirModalEdicion}
            >
              Editar perfil
            </button>

            {perfilId != null && (
              <Button label={'Cerrar sesion'} onClick={() => setShowConfirmModal(true)} />
            )}
          </div>
        </div>
      </div>

      {/* STATS */}
      <div
        style={{
          backgroundColor: '#0f3d1f',
          width: '100vw',
          position: 'relative',
          left: '50%',
          marginLeft: '-50vw',
        }}
      >
        <div className="mx-auto d-flex text-center text-white" style={{ maxWidth: '60%' }}>
          {stats.map((stat, i) => (
            <div
              key={i}
              className="flex-fill py-3"
              style={{
                borderRight: i !== 3 ? '1px solid rgba(255,255,255,0.2)' : 'none',
              }}
            >
              <div className="fw-bold" style={{ color: '#FFC107', fontSize: '2rem' }}>
                {stat.valor}
              </div>
              <div style={{ fontSize: '1rem', marginTop: '2px' }}>{stat.nombre}</div>
            </div>
          ))}
        </div>
      </div>

      {/* CONTENIDO */}
      <div className="mx-auto mt-4 px-3" style={{ maxWidth: '1100px', width: '100%' }}>
        <div className="mb-4">
          <h5 className="mb-3 fw-bold">Últimas calificaciones</h5>

          <div className="d-flex flex-column gap-3">
            {loadingNotificaciones ? (
              <p className="text-muted">Cargando reseñas...</p>
            ) : reviews.contenido?.length === 0 ? (
              <p className="text-muted">Este usuario no tiene reseñas disponibles</p>
            ) : (
              reviews.contenido?.map((r, i) => (
                <div
                  key={i}
                  className="p-3 bg-white rounded shadow-sm d-flex align-items-center gap-3"
                >
                  <div
                    style={{
                      width: '50px',
                      height: '50px',
                      borderRadius: '50%',
                      backgroundColor: '#ccc',
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '16px',
                      fontWeight: 'bold',
                    }}
                  >
                    {r.iniciales}
                  </div>

                  <div className="flex-grow-1">
                    <strong>{r.nombre}</strong>
                    <div>
                      {renderStars(r.valor)} {r.valor}/5
                    </div>
                    <p className="mb-0 text-muted" style={{ fontSize: '0.9rem' }}>
                      {r.descripcion}"
                    </p>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
        <Paginacion
          page={pagina}
          totalPages={reviews.cantidad_de_paginas ?? 1}
          onChange={setPagina}
        />
      </div>

      {/* MODAL */}
      {showModal && (
        <div
          onClick={() => setShowModal(false)}
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100vw',
            height: '100vh',
            backgroundColor: 'rgba(0,0,0,0.5)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 999,
            padding: '16px',
          }}
        >
          <div
            onClick={(e) => e.stopPropagation()}
            style={{
              backgroundColor: 'white',
              borderRadius: '16px',
              width: 'min(1100px, 100%)',
              maxHeight: '92vh',
              display: 'flex',
              flexDirection: 'column',
              overflow: 'hidden',
            }}
          >
            {/* Cabecera */}
            <div style={{ padding: '36px 40px 24px', position: 'relative', flexShrink: 0 }}>
              <h4 style={{ fontWeight: '700', margin: 0, fontSize: '1.4rem' }}>Editar perfil</h4>
              <p style={{ color: '#6c757d', margin: '8px 0 0', fontSize: '0.9rem' }}>
                Actualizá tus datos y medios de contacto
              </p>
              <button
                onClick={() => setShowModal(false)}
                style={{
                  position: 'absolute',
                  top: '24px',
                  right: '24px',
                  width: '34px',
                  height: '34px',
                  borderRadius: '50%',
                  border: '1px solid #dee2e6',
                  background: 'white',
                  cursor: 'pointer',
                  fontSize: '14px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                }}
              >
                ✕
              </button>
            </div>

            {/* Cuerpo scrollable */}
            <div style={{ overflowY: 'auto', flex: 1, padding: '0 40px 56px' }}>
              {/* Tarjeta de preview */}
              <div
                style={{
                  backgroundColor: '#f8f9fa',
                  borderRadius: '12px',
                  padding: '22px 26px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '20px',
                  marginBottom: '36px',
                }}
              >
                <div
                  style={{
                    width: '56px',
                    height: '56px',
                    borderRadius: '50%',
                    backgroundColor: '#175A2D',
                    color: 'white',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '18px',
                    fontWeight: '700',
                    flexShrink: 0,
                  }}
                >
                  {perfil.iniciales ?? '?'}
                </div>
                <div>
                  <div style={{ fontWeight: '600', fontSize: '1.05rem', lineHeight: 1.3 }}>
                    {perfil.nombre}
                  </div>
                  <div style={{ color: '#6c757d', fontSize: '0.85rem', marginTop: '2px' }}>
                    @{perfil.nombre_usuario}
                  </div>
                  <div style={{ fontSize: '0.85rem', color: '#f59e0b', marginTop: '3px' }}>
                    {renderStars(Number(promedio))}{' '}
                    <span style={{ color: '#6c757d' }}>
                      {promedio} ({reviews.cantidad_de_elementos ?? 0})
                    </span>
                  </div>
                </div>
              </div>

              {/* Dos columnas — responsivo */}
              <div className="row g-4 g-lg-5">
                {/* Columna izquierda */}
                <div className="col-12 col-md-6">
                  {/* Perfil público */}
                  <div className="d-flex align-items-center gap-2 mb-3">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="16"
                      height="16"
                      fill="#175A2D"
                      viewBox="0 0 16 16"
                    >
                      <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6Zm2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0Zm4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4Zm-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.029 10 8 10c-2.029 0-3.516.68-4.168 1.332-.678.678-.83 1.418-.832 1.664h10Z" />
                    </svg>
                    <span style={{ color: '#175A2D', fontWeight: '600', fontSize: '0.95rem' }}>
                      Perfil público
                    </span>
                  </div>
                  <p style={{ color: '#6c757d', fontSize: '0.83rem', marginBottom: '20px' }}>
                    Este nombre será visible para otros usuarios
                  </p>
                  <label className="form-label">Nombre</label>
                  <input
                    className="form-control mb-5"
                    value={nombreEditando}
                    onChange={(e) => setNombreEditando(e.target.value)}
                  />

                  <hr style={{ margin: '0 0 32px' }} />

                  {/* Medios de contacto */}
                  <div className="d-flex align-items-center gap-2 mb-3">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="16"
                      height="16"
                      fill="#175A2D"
                      viewBox="0 0 16 16"
                    >
                      <path d="M0 4a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V4Zm2-1a1 1 0 0 0-1 1v.217l7 4.2 7-4.2V4a1 1 0 0 0-1-1H2Zm13 2.383-4.708 2.825L15 11.105V5.383Zm-.034 6.876-5.64-3.471L8 9.583l-1.326-.795-5.64 3.47A1 1 0 0 0 2 13h12a1 1 0 0 0 .966-.741ZM1 11.105l4.708-2.897L1 5.383v5.722Z" />
                    </svg>
                    <span style={{ color: '#175A2D', fontWeight: '600', fontSize: '0.95rem' }}>
                      Medios de contacto
                    </span>
                  </div>
                  <p style={{ color: '#6c757d', fontSize: '0.83rem', marginBottom: '20px' }}>
                    Gestioná cómo los demás pueden contactarte
                  </p>

                  {mediosEditando.length === 0 && (
                    <p className="text-muted mb-4" style={{ fontSize: '0.85rem' }}>
                      Sin medios configurados
                    </p>
                  )}

                  <div className="d-flex flex-column gap-4 mb-4">
                    {mediosEditando.map((m, i) => (
                      <div key={i} className="d-flex align-items-start gap-2">
                        {indiceMedioEditando === i ? (
                          <>
                            <select
                              className="form-select form-select-sm"
                              style={{ width: '120px' }}
                              value={medioEditandoData.medio_comunicacion}
                              onChange={(e) =>
                                setMedioEditandoData((prev) => ({
                                  ...prev,
                                  medio_comunicacion: e.target.value,
                                }))
                              }
                            >
                              <option value="TELEGRAM">Telegram</option>
                              <option value="MAIL">Mail</option>
                            </select>
                            <input
                              className="form-control form-control-sm"
                              style={{ flex: 1 }}
                              value={medioEditandoData.valor}
                              onChange={(e) =>
                                setMedioEditandoData((prev) => ({ ...prev, valor: e.target.value }))
                              }
                            />
                            <button
                              className="btn btn-sm btn-success"
                              onClick={() => confirmarEdicionMedio(i)}
                            >
                              ✓
                            </button>
                            <button
                              className="btn btn-sm btn-outline-secondary"
                              onClick={() => setIndiceMedioEditando(-1)}
                            >
                              ✕
                            </button>
                          </>
                        ) : (
                          <>
                            <div
                              style={{
                                width: '44px',
                                height: '44px',
                                borderRadius: '50%',
                                backgroundColor:
                                  m.medio_comunicacion === 'TELEGRAM' ? '#0088cc' : '#ffc107',
                                display: 'flex',
                                alignItems: 'center',
                                justifyContent: 'center',
                                color: m.medio_comunicacion === 'TELEGRAM' ? 'white' : '#212529',
                                flexShrink: 0,
                              }}
                            >
                              {m.medio_comunicacion === 'TELEGRAM' ? (
                                <svg
                                  xmlns="http://www.w3.org/2000/svg"
                                  width="22"
                                  height="22"
                                  fill="white"
                                  viewBox="0 0 16 16"
                                >
                                  <path d="M16 8A8 8 0 1 1 0 8a8 8 0 0 1 16 0zM8.287 5.906c-.778.324-2.334.994-4.666 2.01-.378.15-.577.298-.595.442-.03.243.275.339.69.47l.175.055c.408.133.958.288 1.243.294.26.006.549-.1.868-.32 2.179-1.471 3.304-2.214 3.374-2.23.05-.012.12-.026.166.016.047.041.042.12.037.141-.03.129-1.227 1.241-1.846 1.817-.193.18-.33.307-.358.336a8.154 8.154 0 0 1-.188.186c-.38.366-.664.64.015 1.088.327.216.589.393.85.571.284.194.568.387.936.629.093.06.183.125.27.187.331.236.63.448.997.414.214-.02.435-.22.547-.82.265-1.417.786-4.486.906-5.751a1.426 1.426 0 0 0-.013-.315.337.337 0 0 0-.114-.217.526.526 0 0 0-.31-.093c-.3.005-.763.166-2.984 1.09z" />
                                </svg>
                              ) : (
                                <svg
                                  xmlns="http://www.w3.org/2000/svg"
                                  width="22"
                                  height="22"
                                  fill="#212529"
                                  viewBox="0 0 16 16"
                                >
                                  <path d="M0 4a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v8a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V4Zm2-1a1 1 0 0 0-1 1v.217l7 4.2 7-4.2V4a1 1 0 0 0-1-1H2Zm13 2.383-4.708 2.825L15 11.105V5.383Zm-.034 6.876-5.64-3.471L8 9.583l-1.326-.795-5.64 3.47A1 1 0 0 0 2 13h12a1 1 0 0 0 .966-.741ZM1 11.105l4.708-2.897L1 5.383v5.722Z" />
                                </svg>
                              )}
                            </div>
                            <div style={{ flex: 1 }}>
                              <div style={{ fontSize: '1rem', fontWeight: '500' }}>{m.valor}</div>
                              <span
                                className="badge mt-1"
                                style={{
                                  backgroundColor: '#175A2D',
                                  fontSize: '0.75rem',
                                  padding: '3px 8px',
                                }}
                              >
                                {m.medio_comunicacion === 'TELEGRAM' ? 'Telegram' : 'Mail'}
                              </span>
                            </div>
                            <button
                              className="btn btn-outline-secondary"
                              style={{ padding: '6px 12px' }}
                              onClick={() => {
                                setIndiceMedioEditando(i)
                                setMedioEditandoData({ ...m })
                              }}
                            >
                              <svg
                                xmlns="http://www.w3.org/2000/svg"
                                width="15"
                                height="15"
                                fill="currentColor"
                                viewBox="0 0 16 16"
                              >
                                <path d="M12.146.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1 0 .708l-10 10a.5.5 0 0 1-.168.11l-5 2a.5.5 0 0 1-.65-.65l2-5a.5.5 0 0 1 .11-.168l10-10zM11.207 2.5 13.5 4.793 14.793 3.5 12.5 1.207 11.207 2.5zm1.586 3L10.5 3.207 4 9.707V10h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.293l6.5-6.5zm-9.761 5.175-.106.106-1.528 3.821 3.821-1.528.106-.106A.5.5 0 0 1 5 12.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.468-.325z" />
                              </svg>
                            </button>
                            <button
                              className="btn btn-outline-danger"
                              style={{ padding: '6px 12px' }}
                              onClick={() => eliminarMedio(i)}
                            >
                              <svg
                                xmlns="http://www.w3.org/2000/svg"
                                width="15"
                                height="15"
                                fill="currentColor"
                                viewBox="0 0 16 16"
                              >
                                <path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5Zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5Zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6Z" />
                                <path d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1ZM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118ZM2.5 3h11V2h-11v1Z" />
                              </svg>
                            </button>
                          </>
                        )}
                      </div>
                    ))}
                  </div>

                  {/* Agregar nuevo medio */}
                  <div className="row g-2">
                    <div className="col-12 col-sm-auto">
                      <select
                        className="form-select w-100"
                        style={{ minWidth: '140px' }}
                        value={nuevoMedioTipo}
                        onChange={(e) => setNuevoMedioTipo(e.target.value)}
                      >
                        <option value="TELEGRAM">Telegram</option>
                        <option value="MAIL">Mail</option>
                      </select>
                    </div>
                    <div className="col">
                      <input
                        className="form-control w-100"
                        placeholder="Ej: @usuario"
                        value={nuevoMedioValor}
                        onChange={(e) => setNuevoMedioValor(e.target.value)}
                      />
                    </div>
                    <div className="col-12 col-sm-auto">
                      <button
                        className="btn w-100"
                        style={{ backgroundColor: '#175A2D', color: 'white', whiteSpace: 'nowrap' }}
                        onClick={agregarMedio}
                      >
                        + Agregar
                      </button>
                    </div>
                  </div>
                </div>

                {/* Columna derecha — Cuenta */}
                <div className="col-12 col-md-6">
                  <div className="d-flex align-items-center gap-2 mb-3">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="16"
                      height="16"
                      fill="#175A2D"
                      viewBox="0 0 16 16"
                    >
                      <path d="M8 1a2 2 0 0 1 2 2v4H6V3a2 2 0 0 1 2-2Zm3 6V3a3 3 0 0 0-6 0v4a2 2 0 0 0-2 2v5a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2Z" />
                    </svg>
                    <span style={{ color: '#175A2D', fontWeight: '600', fontSize: '0.95rem' }}>
                      Cuenta
                    </span>
                  </div>
                  <p style={{ color: '#6c757d', fontSize: '0.83rem', marginBottom: '20px' }}>
                    Actualizá tu nombre de usuario y contraseña
                  </p>

                  <label className="form-label">Nombre de usuario</label>
                  <input
                    className="form-control mb-5"
                    value={nombreUsuarioEditando}
                    onChange={(e) => setNombreUsuarioEditando(e.target.value)}
                  />

                  <label className="form-label">Contraseña actual</label>
                  <div className="input-group mb-5">
                    <input
                      className="form-control"
                      type={showContraseniaActual ? 'text' : 'password'}
                      placeholder="Dejar vacío para no cambiar"
                      value={contraseniaActual}
                      onChange={(e) => setContraseniaActual(e.target.value)}
                    />
                    <button
                      className="btn btn-outline-secondary"
                      type="button"
                      onClick={() => setShowContraseniaActual((v) => !v)}
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="15"
                        height="15"
                        fill="currentColor"
                        viewBox="0 0 16 16"
                      >
                        <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z" />
                        <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z" />
                      </svg>
                    </button>
                  </div>

                  <label className="form-label">Nueva contraseña</label>
                  <div className="input-group mb-3">
                    <input
                      className="form-control"
                      type={showContraseniaNueva ? 'text' : 'password'}
                      placeholder="Dejar vacío para no cambiar"
                      value={contraseniaNueva}
                      onChange={(e) => setContraseniaNueva(e.target.value)}
                    />
                    <button
                      className="btn btn-outline-secondary"
                      type="button"
                      onClick={() => setShowContraseniaNueva((v) => !v)}
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        width="15"
                        height="15"
                        fill="currentColor"
                        viewBox="0 0 16 16"
                      >
                        <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z" />
                        <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z" />
                      </svg>
                    </button>
                  </div>
                  <p style={{ color: '#6c757d', fontSize: '0.8rem' }}>
                    Usá al menos 8 caracteres si querés cambiarla
                  </p>
                </div>
              </div>
            </div>

            {/* Footer fijo */}
            <div
              style={{
                borderTop: '1px solid #e9ecef',
                flexShrink: 0,
                padding: '28px 40px',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                flexWrap: 'wrap',
                gap: '12px',
              }}
            >
              <div
                className="d-flex align-items-center gap-2"
                style={{ color: '#6c757d', fontSize: '0.85rem' }}
              >
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  width="16"
                  height="16"
                  fill="#175A2D"
                  viewBox="0 0 16 16"
                >
                  <path d="M5.338 1.59a61.44 61.44 0 0 0-2.837.856.481.481 0 0 0-.328.39c-.554 4.157.726 7.19 2.253 9.188a10.725 10.725 0 0 0 2.287 2.233c.346.244.652.42.893.533.12.057.218.095.293.118a.55.55 0 0 0 .101.025.615.615 0 0 0 .1-.025c.076-.023.174-.061.294-.118.24-.113.547-.29.893-.533a10.726 10.726 0 0 0 2.287-2.233c1.527-1.997 2.807-5.031 2.253-9.188a.48.48 0 0 0-.328-.39c-.651-.213-1.75-.56-2.837-.856C9.552 1.29 8.531 1.067 8 1.067c-.53 0-1.552.223-2.662.524zM5.072.56C6.157.265 7.31 0 8 0s1.843.265 2.928.56c1.11.3 2.229.655 2.887.87a1.54 1.54 0 0 1 1.044 1.262c.596 4.477-.787 7.795-2.465 9.99a11.775 11.775 0 0 1-2.517 2.453 7.159 7.159 0 0 1-1.048.625c-.28.132-.581.24-.829.24s-.548-.108-.829-.24a7.158 7.158 0 0 1-1.048-.625 11.777 11.777 0 0 1-2.517-2.453C1.928 10.487.545 7.169 1.141 2.692A1.54 1.54 0 0 1 2.185 1.43 62.456 62.456 0 0 1 5.072.56z" />
                </svg>
                Los cambios se guardan en tu perfil público
              </div>
              <div className="d-flex gap-2">
                <button
                  className="btn btn-outline-secondary px-4"
                  onClick={() => setShowModal(false)}
                >
                  Cancelar
                </button>
                <button
                  className="btn px-4"
                  style={{ backgroundColor: '#175A2D', color: 'white' }}
                  onClick={guardarCambios}
                >
                  Guardar cambios
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      <ConfirmModal
        show={showConfirmModal}
        titulo={'Esta seguro que quiere cerrar su sesion?'}
        labelConfirmar={'Aceptar'}
        onConfirmar={manejarCierreDeSesion}
        onCancelar={() => setShowConfirmModal(false)}
      />
    </div>
  )
}

export default Perfil
