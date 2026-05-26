import styles from './button.module.css'

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
      className={`btn ${styles.button} ${styles[variante] ?? ''} ${relevant ? styles.relevant : ''} ${className}`}
      onClick={!disabled ? onClick : undefined}
      disabled={disabled}
    >
      {children || label}
    </button>
  )
}

export default Button