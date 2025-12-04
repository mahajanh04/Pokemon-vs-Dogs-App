import styles from "../../styles/cards/AmazonCard.module.css";

export default function AmazonCard({product, dogsNeeded, additionalStyles }) {

  return (
    <div className={`${styles.productCard} ${additionalStyles}`}>
        {/* Picture for a card */}
        <div className={styles.imageContainer}>
            <img
                src={product.product_photo}
                alt={product.product_title}
                className={styles.productImage}
            />
        </div>
        <div className={styles.textContainer}>
            {/* Title for Amazon card */}
            <h3 className={styles.productTitle}>{product.product_title}</h3>
            {/* Prize for Amazon card */}
            <p className={styles.productPrice}>Price: {product.product_price_eur}</p>
            {/* Quantity of products you can buy for Amazon card */}
            <p className={styles.parameters}>
                Quantity you can buy: {product.quantity_can_buy * dogsNeeded}
            </p>
            {/* Link to Amazon product */}
            <a className={styles.productLink} href={product.product_url} target="_blank" rel="noopener noreferrer">
                View Product
            </a>
        </div>
    </div>
  );
}
