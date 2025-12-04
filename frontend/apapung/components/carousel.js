import React, { useCallback } from 'react';
import useEmblaCarousel from 'embla-carousel-react';
import styles from '../styles/Carousel.module.css';
import AmazonCard from './cards/amazonCard';

export default function Carousel({ products, dogsNeeded }) {
  
    const [emblaRef, emblaApi] = useEmblaCarousel({ 
        loop: true,
        dragFree: true,
        speed: 10,
    });

  const scrollNext = useCallback(() => {
    if (emblaApi) emblaApi.scrollNext();
  }, [emblaApi]);

  const scrollPrev = useCallback(() => {
    if (emblaApi) emblaApi.scrollPrev();
  }, [emblaApi]);

  return (
    <div className={styles.carouselContainer}>
      
      <div className={styles.embla}>
        <div className={styles.emblaViewport} ref={emblaRef}>
          <div className={styles.emblaContainer}>
            
            {Array.isArray(products) && products.map((slide, index) => (
              <div className={styles.emblaSlide} key={index}>
                <AmazonCard product={slide} dogsNeeded={dogsNeeded} />
              </div>
            ))}

          </div>
        </div>

        <button className={`${styles.navButton} ${styles.prev}`} onClick={scrollPrev}>
          &#9664;
        </button>
        
        <button className={`${styles.navButton} ${styles.next}`} onClick={scrollNext}>
          &#9654;
        </button>
      
      </div>
    </div>
  );
}
