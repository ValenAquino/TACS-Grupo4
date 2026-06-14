import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { crearAdministrador, crearUsuario } from '@/services/usuarioService.js'
import { useToast } from '@/contexts/toastContext.jsx'
import { useAuth } from '@/contexts/userContext.jsx'
import { useError } from '@/contexts/errorContext.jsx'
import ModalInformativo from '@/components/ui/modales/modal-informativo/modal-informativo.jsx'
import { IconoOjoTachado } from '@/components/ui/iconos/ojo-tachado/ojo-tachado.jsx'
import { IconoOjo } from '@/components/ui/iconos/ojo/ojo.jsx'
import styles from './registrar.module.css'

function Registrar() {
  const [formData, setFormData] = useState({
    nombre: '',
    contrasenia: '',
    confirmarContrasenia: '',
    rol: 'USUARIO',
  })

  const { handleError, errorTemplate } = useError()

  const [errorState, setErrorState] = useState(
    errorTemplate({ nombre: undefined, contrasenia: undefined }),
  )
  const [onSubmit, setOnSubmit] = useState(false)

  // --- Visibilidad de contraseñas ---
  const [mostrarContrasenia, setMostrarContrasenia] = useState(false)
  const [mostrarConfirmacion, setMostrarConfirmacion] = useState(false)

  const { showToast } = useToast()
  const { user } = useAuth()
  const navigate = useNavigate()

  // --- Validación de fuerza de contraseña en tiempo real ---
  const validarContrasenia = (contrasenia) => {
    return {
      longitud: contrasenia.length >= 8,
      mayuscula: /[A-Z]/.test(contrasenia),
      minuscula: /[a-z]/.test(contrasenia),
      numero: /[0-9]/.test(contrasenia),
      especial: /[^A-Za-z0-9]/.test(contrasenia),
    }
  }

  const passwordChecks = validarContrasenia(formData.contrasenia)
  const passwordEsValida = Object.values(passwordChecks).every(Boolean)

  // --- Coincidencia de contraseñas en tiempo real ---
  const contraseniasCoinciden =
    formData.confirmarContrasenia.length > 0 &&
    formData.contrasenia === formData.confirmarContrasenia

  const mostrarErrorCoincidencia =
    formData.confirmarContrasenia.length > 0 && !contraseniasCoinciden

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!formData.nombre || !formData.contrasenia || !formData.confirmarContrasenia) {
      showToast('Completa todos los campos', 'error')
      return
    }

    if (!passwordEsValida) {
      showToast('La contraseña no cumple con los requisitos de seguridad', 'error')
      return
    }

    if (formData.contrasenia !== formData.confirmarContrasenia) {
      showToast('Las contraseñas no coinciden', 'error')
      return
    }

    const usuario = {
      nombre: formData.nombre,
      contrasenia: formData.contrasenia,
      rol: formData.rol,
    }

    try {
      setOnSubmit(true)
      if (user?.rol === 'ADMINISTRADOR') {
        await crearAdministrador(usuario)
      } else {
        await crearUsuario(usuario)
      }
      showToast(`Usuario creado correctamente`)
      navigate('/')
    } catch (error) {
      showToast(handleError(error, setErrorState), 'error')
    } finally {
      setOnSubmit(false)
    }
  }

  const RequisitoItem = ({ cumplido, texto }) => (
    <li className={cumplido ? styles.textoValido : styles.textoPendiente}>
      <span className={styles.icono}>{cumplido ? '✓' : '○'}</span>
      {texto}
    </li>
  )

  return (
    <div
      className="container-fluid d-flex justify-content-center align-items-center py-5"
      style={{
        minHeight: '100vh',
        backgroundColor: 'var(--color-background)',
      }}
    >
      <div
        className="card shadow p-4"
        style={{
          width: '100%',
          maxWidth: '450px',
          backgroundColor: 'var(--color-primary)',
          border: 'var(--border-weight) solid var(--border-color-light)',
          borderRadius: '16px',
        }}
      >
        <h1
          className="text-center mb-2"
          style={{
            color: 'var(--color-secondary)',
          }}
        >
          Registrarse
        </h1>

        <p
          className="text-center mb-4"
          style={{
            color: 'var(--color-subtitle)',
          }}
        >
          Creá una cuenta nueva
        </p>

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Nombre</label>

            <input
              type="text"
              name="nombre"
              className="form-control"
              placeholder="Juan Pérez"
              value={formData.nombre}
              onChange={handleChange}
              style={{
                borderColor: 'var(--border-color-dark)',
              }}
            />
          </div>

          <div className="mb-3">
            <label className="form-label">Contraseña</label>

            <div className="input-group">
              <input
                type={mostrarContrasenia ? 'text' : 'password'}
                name="contrasenia"
                className="form-control"
                placeholder="********"
                value={formData.contrasenia}
                onChange={handleChange}
                style={{
                  borderColor: 'var(--border-color-dark)',
                }}
              />
              <button
                type="button"
                className="btn"
                onClick={() => setMostrarContrasenia(!mostrarContrasenia)}
                style={{
                  borderColor: 'var(--border-color-dark)',
                  color: 'var(--color-subtitle)',
                }}
                aria-label={mostrarContrasenia ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                tabIndex={-1}
              >
                {mostrarContrasenia ? <IconoOjoTachado /> : <IconoOjo />}
              </button>
            </div>

            {formData.contrasenia.length > 0 &&
              (passwordEsValida ? (
                <small className={styles.textoValido}>
                  <span className={styles.icono}>✓</span> Contraseña segura
                </small>
              ) : (
                <ul className={`list-unstyled mt-2 mb-0 ${styles.listaRequisitos}`}>
                  <RequisitoItem cumplido={passwordChecks.longitud} texto="Al menos 8 caracteres" />
                  <RequisitoItem cumplido={passwordChecks.mayuscula} texto="Al menos 1 mayúscula" />
                  <RequisitoItem cumplido={passwordChecks.minuscula} texto="Al menos 1 minúscula" />
                  <RequisitoItem cumplido={passwordChecks.numero} texto="Al menos 1 número" />
                  <RequisitoItem
                    cumplido={passwordChecks.especial}
                    texto="Al menos 1 carácter especial"
                  />
                </ul>
              ))}
          </div>

          <div className="mb-4">
            <label className="form-label">Confirmar contraseña</label>

            <div className="input-group">
              <input
                type={mostrarConfirmacion ? 'text' : 'password'}
                name="confirmarContrasenia"
                className={`form-control ${
                  mostrarErrorCoincidencia
                    ? styles.inputInvalido
                    : contraseniasCoinciden
                      ? styles.inputValido
                      : ''
                }`}
                placeholder="********"
                value={formData.confirmarContrasenia}
                onChange={handleChange}
                style={{
                  borderColor:
                    !mostrarErrorCoincidencia && !contraseniasCoinciden
                      ? 'var(--border-color-dark)'
                      : undefined,
                }}
              />
              <button
                type="button"
                className="btn"
                onClick={() => setMostrarConfirmacion(!mostrarConfirmacion)}
                style={{
                  borderColor: 'var(--border-color-dark)',
                  color: 'var(--color-subtitle)',
                }}
                aria-label={mostrarConfirmacion ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                tabIndex={-1}
              >
                {mostrarConfirmacion  ? <IconoOjoTachado /> : <IconoOjo />}
              </button>
            </div>

            {formData.confirmarContrasenia.length > 0 && (
              <small
                className={`form-text ${contraseniasCoinciden ? styles.textoValido : styles.textoInvalido}`}
              >
                {contraseniasCoinciden
                  ? '✓ Las contraseñas coinciden'
                  : '✗ Las contraseñas no coinciden'}
              </small>
            )}
          </div>

          {user?.rol === 'ADMINISTRADOR' && (
            <div className="mb-4">
              <label className="form-label">Rol</label>
              <select
                className="form-select"
                name="rol"
                onChange={handleChange}
                value={formData.rol}
              >
                <option value="USUARIO">USUARIO</option>
                <option value="ADMINISTRADOR">ADMINISTRADOR</option>
              </select>
            </div>
          )}

          <button
            type="submit"
            className="btn w-100 fw-bold"
            disabled={!passwordEsValida || !contraseniasCoinciden}
            style={{
              backgroundColor: 'var(--color-secondary)',
              color: 'white',
            }}
          >
            Crear cuenta
          </button>
        </form>

        <div className="text-center mt-4">
          <p>
            ¿Ya tenés cuenta?{' '}
            <Link
              to="/login"
              style={{
                color: 'var(--color-terciary)',
                textDecoration: 'none',
                fontWeight: '600',
              }}
            >
              Iniciar sesión
            </Link>
          </p>
        </div>
      </div>
      <ModalInformativo open={onSubmit}>
        <h3>Registrando usuario...</h3>
        <p>Esto puede tardar unos segundos</p>
      </ModalInformativo>
    </div>
  )
}

export default Registrar
