import styles from './button.module.css'

const varianteClase = {
  default: '',
  peligro: 'btn-danger',
  exito: 'btn-success',
  peligroBorde: 'btn-outline-danger',
  exitoBorde: 'btn-outline-success',
  secundarioBorde: 'btn-outline-secondary',
}

const Button = ({
  children,
  label,
  onClick,
  className = '',
  disabled = false,
  relevant = false,
  type = 'button',
  variante = 'default',
}) => {
  return (
    <button
      type={type}
      className={`btn ${styles.button} ${varianteClase[variante]} ${relevant ? styles.relevant : ''} ${className}`}
      onClick={!disabled ? onClick : undefined}
      disabled={disabled}
    >
      {children || label}
    </button>
  );
};

export default Button
