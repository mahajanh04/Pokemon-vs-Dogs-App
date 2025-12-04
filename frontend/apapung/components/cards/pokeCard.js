import styles from "../../styles/cards/PokeCard.module.css";

export default function PokeCard({ name, image, dexNum, types, power, additionalStyles }) {

  if(name != null){
    return (
    <div className={`${styles.cardContainer} ${additionalStyles}`}>
      <div className={styles.card}>
        
        <img src={image} alt={name} className={styles.cardImage} />
        
        <div className={styles.infoContainer}>
          {/* Name for the dog/pokemon card */}
          <h2 className={styles.cardName}>Name:{name}</h2>
          
          <div className={styles.parameterContainer}>
              <p className={styles.cardParameter}>
                <span className={styles.parameterKey}>Dex number: </span>
                <span className={styles.parameterValue} >{dexNum}</span>
              </p>
          </div>

          {/* Pokémon Types as images */}
          {types.length > 0 && (
            <div className={styles.parameterContainer}>
                <div className={styles.types}>
                {types.map((typeUrl, index) => (
                  <img
                    key={index}
                    src={typeUrl}
                    alt={`Type ${index}`}
                    className={styles.typeImage}
                  />
                ))}
                </div>
            </div>
          )}

          <div className={styles.parameterContainer}>
              <p className={styles.cardParameter}>
                <span className={styles.parameterKey}>Base Power:</span>
                <span className={styles.parameterValue}>{power}</span>
              </p>
          </div>
        </div>
      </div>
    </div>
  );}
  else {
    return(
      <div className={`${styles.cardContainer} ${additionalStyles}`}>
      <div className={styles.card}>
        
        <img src={image} alt={"Not Found"} className={styles.cardImage} />
        
        <div className={styles.infoContainer}>
          {/* Name for the dog/pokemon card */}
          <h2 className={styles.cardName}>Pokemon not found</h2>
          
          <div className={styles.parameterContainer}>
              <p className={styles.cardParameter}>
                <span className={styles.parameterValue} >press the button below to try again</span>
              </p>
          </div>
        </div>
      </div>
    </div>
    );
  }
}
