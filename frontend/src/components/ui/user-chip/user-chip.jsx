import styles from './user-chip.module.css'

const UserChip = ({ user }) => (
  <div className={styles.chip}>
    <span className={styles.avatar} style={{ background: user.color }}>
      {user.initials}
    </span>
    <span className={styles.name}>{user.name}</span>
    <span className={styles.stars}>
      {'★'.repeat(user.stars)}{'☆'.repeat(5 - user.stars)}
    </span>
  </div>
)

export default UserChip
