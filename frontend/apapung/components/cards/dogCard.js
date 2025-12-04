import styles from "../../styles/cards/DogCard.module.css";

export default function DogCard({ name, image, weight, height, lifespan, strength, additionalStyles }) {

  if(name != null){
    return (
      <div className={`${styles.cardContainer} ${additionalStyles}`}>
        <div className={styles.card}>
          
          <img src={image} alt={name} className={styles.cardImage} />
          
          <div className={styles.infoContainer}>
            {/* Name for the dog card */}
            <h2 className={styles.cardName}>Name:{name}</h2>
            {/* Dogs weight */}
            <div className={styles.parameterContainer}>
                <p className={styles.cardParameter}>
                  <span className={styles.parameterKey}>Weight:</span>
                  <span className={styles.parameterValue} >{weight} kg</span>
                </p>
            </div>
              {/* Dogs height */}
            <div className={styles.parameterContainer}>
                <p className={styles.cardParameter}>
                  <span className={styles.parameterKey}>Height:</span>
                  <span className={styles.parameterValue}>{height} cm</span>
                </p>
            </div>
              {/* Dogs lifespan */}
            <div className={styles.parameterContainer}>
                <p className={styles.cardParameter}>
                  <span className={styles.parameterKey}>Lifespan:</span>
                  <span className={styles.parameterValue}>{lifespan}</span>
                </p>
            </div>
              {/* Dogs power (always ????) */}
            <div className={styles.parameterContainer}>
                <p className={styles.cardParameter}>
                  <span className={styles.parameterKey}>Base power:</span>
                  <span className={styles.parameterValue}>{strength}</span>
                </p>
            </div>
          </div>
        </div>
      </div>
    );
  } else {
    return(
      <div className={`${styles.cardContainer} ${additionalStyles}`}>
      <div className={styles.card}>
        
        <img src={image} alt={name} className={styles.cardImage} />
        
        <div className={styles.infoContainer}>
          {/* Name for the dog card */}
          <h2 className={styles.cardName}>Dog is not found</h2>
          {/* Dogs weight */}
          <div className={styles.parameterContainer}>
              <p className={styles.cardParameter}>
                <span className={styles.parameterValue} >Press the button above to try again</span>
              </p>
          </div>
        </div>
      </div>
    </div>
    );
  }
}
