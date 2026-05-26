import { useState } from 'react'
import styles from './columna.module.css'

const EyeIcon = () => (
  <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" fill="currentColor" viewBox="0 0 16 16">
    <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z" />
    <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z" />
  </svg>
)

const ColumnaContrasenia = ({
  nombreUsuarioEditando,
  setNombreUsuarioEditando,
  contraseniaActual,
  setContraseniaActual,
  contraseniaNueva,
  setContraseniaNueva,
}) => {
  const [showContraseniaActual, setShowContraseniaActual] = useState(false)
  const [showContraseniaNueva, setShowContraseniaNueva] = useState(false)

  return (
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
        <span className={styles['seccion-titulo']}>Cuenta</span>
      </div>
      <p className={styles['seccion-subtitulo']}>Actualizá tu nombre de usuario y contraseña</p>

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
          <EyeIcon />
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
          <EyeIcon />
        </button>
      </div>
      <p className={styles.hint}>Usá al menos 8 caracteres si querés cambiarla</p>
    </div>
  )
}

export default ColumnaContrasenia
