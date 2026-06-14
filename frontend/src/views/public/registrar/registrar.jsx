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
import { IconoAdvertencia } from '@/components/ui/iconos/advertencia/advertencia.jsx'
import { IconoVerificado } from '@/components/ui/iconos/verificado/verificado.jsx'

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

  const [mostrarContrasenia, setMostrarContrasenia] = useState(false)
  const [mostrarConfirmacion, setMostrarConfirmacion] = useState(false)

  const [tocado, setTocado] = useState({
    nombre: false,
    contrasenia: false,
    confirmarContrasenia: false,
  })

  const { showToast } = useToast()
  const { user } = useAuth()
  const navigate = useNavigate()

  const validarNombre = (nombre) => {
    return {
      sinEspacios: !/\s/.test(nombre),
      longitudMinima: nombre.length >= 3,
      caracteresValidos: /^[a-zA-Z0-9_.]*$/.test(nombre),
    }
  }

  const nombreChecks = validarNombre(formData.nombre)
  const nombreEsValido = formData.nombre.length > 0 && Object.values(nombreChecks).every(Boolean)

  const getErrorNombre = () => {
    if (formData.nombre.length === 0) return 'El nombre de usuario no puede estar vacío'
    if (!nombreChecks.sinEspacios) return 'El nombre de usuario no puede contener espacios'
    if (!nombreChecks.longitudMinima) return 'El nombre de usuario debe tener al menos 3 caracteres'
    if (!nombreChecks.caracteresValidos)
      return 'El nombre de usuario contiene caracteres no permitidos'
    return null
  }

  const errorNombre = getErrorNombre()

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

  const getErrorContrasenia = () => {
    if (formData.contrasenia.length === 0) return 'La contraseña no puede estar vacía'
    if (!passwordChecks.longitud) return 'La contraseña es demasiado corta'

    const faltantes = []
    if (!passwordChecks.mayuscula) faltantes.push('una mayúscula')
    if (!passwordChecks.minuscula) faltantes.push('una minúscula')
    if (!passwordChecks.numero) faltantes.push('un número')
    if (!passwordChecks.especial) faltantes.push('un carácter especial')

    if (faltantes.length === 0) return null
    return `La contraseña necesita ${faltantes.join(', ')}`
  }

  const errorContrasenia = getErrorContrasenia()

  const contraseniasCoinciden =
    formData.confirmarContrasenia.length > 0 &&
    formData.contrasenia === formData.confirmarContrasenia

  const getErrorConfirmacion = () => {
    if (formData.confirmarContrasenia.length === 0) return 'Confirmá tu contraseña'
    if (!contraseniasCoinciden) return 'Las contraseñas no coinciden'
    return null
  }

  const errorConfirmacion = getErrorConfirmacion()

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    })
  }

  const handleBlur = (e) => {
    setTocado((prev) => ({ ...prev, [e.target.name]: true }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    setTocado({ nombre: true, contrasenia: true, confirmarContrasenia: true })

    if (errorNombre || errorContrasenia || errorConfirmacion) {
      showToast('Revisá los campos marcados en rojo', 'error')

      if (errorNombre) {
        document.querySelector('input[name="nombre"]')?.focus()
      } else if (errorContrasenia) {
        document.querySelector('input[name="contrasenia"]')?.focus()
      } else {
        document.querySelector('input[name="confirmarContrasenia"]')?.focus()
      }
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
            <label className="form-label">Nombre de usuario</label>
            <div className="input-group">
              <input
                type="text"
                name="nombre"
                className={`form-control ${
                  tocado.nombre && errorNombre
                    ? styles.inputInvalido
                    : tocado.nombre && nombreEsValido
                      ? styles.inputValido
                      : ''
                }`}
                placeholder="juanperez123"
                value={formData.nombre}
                onChange={handleChange}
                onBlur={handleBlur}
                style={{
                  borderColor:
                    !(tocado.nombre && errorNombre) && !(tocado.nombre && nombreEsValido)
                      ? 'var(--border-color-dark)'
                      : undefined,
                }}
              />
              {tocado.nombre && nombreEsValido && (
                <span
                  className="input-group-text"
                  style={{ backgroundColor: 'transparent', borderColor: 'var(--color-success)' }}
                >
                  <IconoVerificado />
                </span>
              )}
            </div>

            {tocado.nombre && errorNombre && (
              <small
                className={`form-text d-flex align-items-center gap-1 mt-1 ${styles.textoInvalido}`}
              >
                <IconoAdvertencia /> {errorNombre}
              </small>
            )}

            <small className={`form-text d-block mt-1 ${styles.textoNeutro}`}>
              Debe ser único, sin espacios. Solo letras, números, "_" o ".".
            </small>
          </div>

          <div className="mb-3">
            <label className="form-label">Contraseña</label>

            <div className="input-group">
              <input
                type={mostrarContrasenia ? 'text' : 'password'}
                name="contrasenia"
                className={`form-control ${
                  tocado.contrasenia && errorContrasenia
                    ? styles.inputInvalido
                    : tocado.contrasenia && passwordEsValida
                      ? styles.inputValido
                      : ''
                }`}
                placeholder="********"
                value={formData.contrasenia}
                onChange={handleChange}
                onBlur={handleBlur}
                style={{
                  borderColor:
                    !(tocado.contrasenia && errorContrasenia) &&
                    !(tocado.contrasenia && passwordEsValida)
                      ? 'var(--border-color-dark)'
                      : undefined,
                }}
              />
              {tocado.contrasenia && passwordEsValida && (
                <span
                  className="input-group-text"
                  style={{ backgroundColor: 'transparent', borderColor: 'var(--color-success)' }}
                >
                  <IconoVerificado />
                </span>
              )}
              <button
                type="button"
                className="btn"
                onClick={() => setMostrarContrasenia(!mostrarContrasenia)}
                style={{ borderColor: 'var(--border-color-dark)', color: 'var(--color-subtitle)' }}
                aria-label={mostrarContrasenia ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                tabIndex={-1}
              >
                {mostrarContrasenia ? <IconoOjoTachado /> : <IconoOjo />}
              </button>
            </div>

            {tocado.contrasenia && errorContrasenia && (
              <small
                className={`form-text d-flex align-items-center gap-1 mt-1 ${styles.textoInvalido}`}
              >
                <IconoAdvertencia /> {errorContrasenia}
              </small>
            )}

            <small className={`form-text d-block mt-1 ${styles.textoNeutro}`}>
              La contraseña debe tener al menos 8 caracteres, incluyendo una mayúscula, una
              minúscula, un número y un carácter especial.
            </small>
          </div>

          <div className="mb-4">
            <label className="form-label">Confirmar contraseña</label>

            <div className="input-group">
              <input
                type={mostrarConfirmacion ? 'text' : 'password'}
                name="confirmarContrasenia"
                className={`form-control ${
                  tocado.confirmarContrasenia && errorConfirmacion
                    ? styles.inputInvalido
                    : tocado.confirmarContrasenia && contraseniasCoinciden
                      ? styles.inputValido
                      : ''
                }`}
                placeholder="********"
                value={formData.confirmarContrasenia}
                onChange={handleChange}
                onBlur={handleBlur}
                style={{
                  borderColor: !(tocado.confirmarContrasenia && errorConfirmacion)
                    ? 'var(--border-color-dark)'
                    : undefined,
                }}
              />
              {tocado.confirmarContrasenia && contraseniasCoinciden && (
                <span
                  className="input-group-text"
                  style={{ backgroundColor: 'transparent', borderColor: 'var(--color-success)' }}
                >
                  <IconoVerificado />
                </span>
              )}
              <button
                type="button"
                className="btn"
                onClick={() => setMostrarConfirmacion(!mostrarConfirmacion)}
                style={{ borderColor: 'var(--border-color-dark)', color: 'var(--color-subtitle)' }}
                aria-label={mostrarConfirmacion ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                tabIndex={-1}
              >
                {mostrarConfirmacion ? <IconoOjoTachado /> : <IconoOjo />}
              </button>
            </div>

            {tocado.confirmarContrasenia && errorConfirmacion && (
              <small
                className={`form-text d-flex align-items-center gap-1 mt-1 ${styles.textoInvalido}`}
              >
                <IconoAdvertencia /> {errorConfirmacion}
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
