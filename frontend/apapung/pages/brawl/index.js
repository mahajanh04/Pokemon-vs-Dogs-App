import Head from "next/head.js";
import Image from "next/image";
import { useState } from "react";
import { useRouter } from "next/router";
import styles from "../../styles/Brawl.module.css";
import CardHolder from "../../components/cardHolder.js";
import PokeCard from "../../components/cards/pokeCard.js";
import DogCard from "../../components/cards/dogCard.js";

export default function Brawl() {
  const router = useRouter();
  const [pokemonData, setPokemonData] = useState(null);
  const [dogData, setDogData] = useState(null);
  const [isBrawling, setIsBrawling] = useState(false);
  const [actualDogName, setActualDogName] = useState("");
  const [isPokemonSelected, setIsPokemonSelected] = useState(false);
  const [isDogSelected, setIsDogSelected] = useState(false);

  const handlePokemonSubmit = async (input) => {
    try {
      // console.log("POKEMON API ACCESS");
      const response = await fetch(
        `http://localhost:8080/pokemon/${input}/stats`
      );
      const data = await response.json();

      //Extract fields for Card component
      const {
        Name: name,
        "Sprite url": image,
        "Total base stats": power,
        Types: types,
        "Dex number": dexNum,
      } = data;

      // Set Pokemon data for Card display
      setPokemonData({
        name,
        image,
        power,
        types,
        dexNum,
      });

      setIsPokemonSelected(true);
    } catch (error) {
      console.error("Error fetching Pokemon data:", error);
    }
  };

  // Handle dog card submission
  const handleDogSubmit = async (input) => {
    setActualDogName(input);
    try {
      // console.log("API ACCESS DOGS");

      const response = await fetch(
        `http://localhost:8080/breed-info/${input}/clean`
      );
      const data = await response.json();

      // decompose data into vars
      const { name, images, lifespan, height, weight, strength } = data;

      // Choose a random image from the images array
      const randomImage =
        images && images.length > 0
          ? images[Math.floor(Math.random() * images.length)]
          : "";

      // Set Dog data for Card display
      setDogData({
        name,
        image: randomImage,
        lifespan,
        height,
        weight,
        strength,
      });

      setIsDogSelected(true);
    } catch (error) {
      console.error("Error fetching Dog data:", error);
    }
  };

  const handleRandomizePokemon = () => {
    const randomPokemonId = Math.floor(Math.random() * 1000) + 1; // Random number between 1 and 1000
    handlePokemonSubmit(randomPokemonId.toString());
  };

  const handleRandomizeDog = async () => {
    
    try {
      const response = await fetch(
        `http://localhost:8080/breed-info/random/clean`
      );

      const data = await response.json();

      // decompose data into vars
      const { name, images, lifespan, height, weight, strength } = data;

      // Choose a random image from the images array
      const randomImage =
        images && images.length > 0
          ? images[Math.floor(Math.random() * images.length)]
          : "";

      // Set Dog data for Card display
      setDogData({
        name,
        image: randomImage,
        lifespan,
        height,
        weight,
        strength,
      });

      setIsDogSelected(true);
    } catch (error) {
      console.error("Error getting random Dog:", error);
    }
    // const dogBreeds = ["bulldog", "labrador", "poodle", "beagle"]; // Example breed list
    // const randomDogBreed =
    //   dogBreeds[Math.floor(Math.random() * dogBreeds.length)];
    
  };

  // Handle VS button click
  const handleVSClick = () => {
    if (pokemonData && dogData) {
      setIsBrawling(true);
      setTimeout(() => {
        // After animation completes (simulate with a timeout), navigate to /or
        router.push({
          pathname: "/or",
          query: {
            pokemon: pokemonData.name,
            dog: dogData.name,
          },
        });
      });
    } else {
      alert("Please select both a Pokemon and a Dog!");
    }
  };

  //Unselect Pokemon
  const unselectPokemon = () => {
    setPokemonData(null);
    setIsPokemonSelected(false);
  };

  //Unselect dog breed
  const unselectDog = () => {
    setDogData(null);
    setIsDogSelected(false);
  };

  return (
    <div className={styles.mainScreenContainer}>
      <Head>
        <title>Apapung</title>
      </Head>
      <div className={styles.pokemonContainer}>
        {!isPokemonSelected && (
          <CardHolder
            startButtonPlaceholder={"Click to choose a pokemon"}
            popUpModalPlaceholder={"Enter pokemon name"}
            onCardSubmit={handlePokemonSubmit}
            onRandomize={handleRandomizePokemon}
            isPokemonSelected={isPokemonSelected}
            isDogSelected={isDogSelected}
          ></CardHolder>
        )}
        {pokemonData && (
          <div>
            <PokeCard
              name={pokemonData.name}
              image={pokemonData.image}
              types={pokemonData.types}
              power={pokemonData.power}
              dexNum={pokemonData.dexNum}
              additionalStyles={styles.pokemonCard}
            />
            <button
              className={styles.unselectButtonPokemon}
              onClick={unselectPokemon}
            >
              Unselect Pokemon
            </button>
          </div>
        )}
      </div>

      <div className={styles.vsContainer}>
        <Image
          src="/Vs.png"
          alt="VS Logo"
          className={`${styles.vsLogo} ${
            isBrawling ? styles.spinAnimation : ""
          }`}
          width={200}
          height={200}
          onClick={handleVSClick}
          priority={false}
        />
      </div>
      <div className={styles.dogContainer}>
        {!isDogSelected && (
          <CardHolder
            startButtonPlaceholder={"Click to choose a dog breed"}
            popUpModalPlaceholder={"Enter dog breed"}
            onCardSubmit={handleDogSubmit}
            onRandomize={handleRandomizeDog}
            isPokemonSelected={isPokemonSelected} // Pass isPokemonSelected state
            isDogSelected={isDogSelected} // Pass isDogSelected state
          ></CardHolder>
        )}
        {dogData && (
          <div>
            <DogCard
              name={dogData.name}
              image={dogData.image}
              weight={dogData.weight}
              height={dogData.height}
              lifespan={dogData.lifespan}
              strength={dogData.strength}
              additionalStyles={styles.dogCard}
            />
            <button className={styles.unselectButtonDog} onClick={unselectDog}>
              Unselect Dog
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
