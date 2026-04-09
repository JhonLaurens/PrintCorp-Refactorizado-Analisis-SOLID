import { ref } from "vue";
import { supabase } from "../supabaseClient";

export function useScores() {
  const scores = ref<any[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  async function fetchScores() {
    loading.value = true;
    const { data, error: err } = await supabase
      .from("scores")
      .select("*")
      .order("score", { ascending: false })
      .limit(10);
    if (err) error.value = err.message;
    scores.value = data || [];
    loading.value = false;
  }

  async function addScore(player: string, score: number) {
    const { error: err } = await supabase
      .from("scores")
      .insert([{ player, score }]);
    if (err) error.value = err.message;
    await fetchScores();
  }

  return { scores, loading, error, fetchScores, addScore };
}
