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

export default renderStars
